package ptv.education.check_podft.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ptv.education.check_podft.model.Document;
import ptv.education.check_podft.model.ListInfo;
import ptv.education.check_podft.model.Subject;
import ptv.education.check_podft.services.DocumentsService;
import ptv.education.check_podft.services.ListInfoService;
import ptv.education.check_podft.services.SubjectsService;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api_load")
public class PodftRESTLoadFileController {

    @Value("${xmlfilepath}")
    private String path;  //имя файла для загрузки берем из настроек  //TODO: взять  из каталога xml-файл с максимальной датой
    private final SubjectsService subjectsService;
    private final ListInfoService listInfoService;
    private final DocumentsService documentsService;

    @Autowired
    public PodftRESTLoadFileController(SubjectsService subjectsService, ListInfoService listInfoService, DocumentsService documentsService) {
        this.subjectsService = subjectsService;
        this.listInfoService = listInfoService;
        this.documentsService = documentsService;
    }
//    @GetMapping("/xml_test")
//    public void loadfile_test(){
//        //ListInfo listInfo = listInfoService.findOne(22);
//        Optional<ListInfo> listInfo =  listInfoService.findLastListDate();
//        if(listInfo.isPresent()){
//            System.out.println(listInfo.get().getListDate());
//        } else{
//            System.out.println("Information about list does not found. We are going to load the first list!!!");
//        }
//    }
    @Tag(name = "Загрузка XML-файла с перечнем экстремистов", description = "Вызов метода иницирует процедуру загрузки файла в БД. Файл лежит в каталоге на диске. Путь к файлу прописан в application.property.  Перед загрузкой " +
            "проверяется акутальность загружаемого файла")
    @GetMapping("/xmlfile")
    public void loadfile(){
        boolean need_reload = true;   // если дата нового перечня больше даты последнего загруженного, то нужно загружать новый перечень. Перед этим удаляем данные из таблиц.
        Subject subject=null;  //ФЛ или ЮЛ
        Document document = null;
        String formatVersion="";
        String xPathStr="";  // строка с содержит Xpath до элемента.   <Идентификатор> Для типа субьекта
        String xPathStrDocType="";  // строка с содержит Xpath до элемента. <наименование>.Для типа документов
        String xPathStrDoc="";  // строка с содержит Xpath до элементов <Документ>.Для документов
        String xPathStrFL="";  // строка с содержит Xpath до элементов  ФЛ
        int Subject_number_in_list =0;   // ИдСубъекта 	 находится в фАЙЛЕ до типа субъекта.  и если  субъект - ЮЛ, то  объект не будет создан и мы не сможем сделать setSubject_number_in_list()

        int loadcount =0;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(path));

            while (reader.hasNext() && need_reload ) {   //&& loadcount<3
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {   //открывающий тэг
                    StartElement startElement = nextEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "ВерсияФормата":
                            nextEvent = reader.nextEvent();
                            formatVersion = nextEvent.asCharacters().getData();
                            break;
                        case "ДатаПеречня" :
							 /*
							 вычитываем из переня его дату и сравниваем с датой перечня в базе. Грузим список только если он новый.
							  */
                            nextEvent = reader.nextEvent();
                            String[] date_parts1 =nextEvent.asCharacters().getData().trim().substring(0,10).split("-");   //значение даты из xml в гггг, мм, дд,
                            LocalDate new_date = LocalDate.of(Integer.parseInt(date_parts1[0]),Integer.parseInt(date_parts1[1]),Integer.parseInt(date_parts1[2])); //собираем LocalDate
                            try {
                                Optional<ListInfo> listInfo =  listInfoService.findLastListDate();  //получаем дату последнего спиcка из базы

                                if(listInfo.isPresent()){
                                    LocalDate saved_date =listInfo.get().getListDate();
                                    if(new_date.isBefore(saved_date) || new_date.isEqual(saved_date) ){   //если дата из xml раньше   или равна сохраненной, то грузить список не нужно.
                                        need_reload=false;
                                    } else{  //иначе записываем в  базу информацию о новом списке  и чистим базу
                                        ListInfo newListInfo =  new ListInfo(formatVersion,new_date,saved_date);
                                        listInfoService.save(newListInfo);
                                        subjectsService.truncateSubjects();

                                    }
                                } else{   //Если дат вообще нет, то тоже создаем запись и чистим базу
                                    ListInfo newListInfo =  new ListInfo(formatVersion,new_date);
                                    listInfoService.save(newListInfo);
                                    subjectsService.truncateSubjects();
                                }
                            } finally{

                            }
                            break;
                        case "Субъект" :
                            xPathStr = "Субъект";
                            break;
                        case "ИдСубъекта":
                            if(xPathStr.equals("Субъект")) {
                                nextEvent = reader.nextEvent();
                                Subject_number_in_list = Integer.parseInt(nextEvent.asCharacters().getData());

                            }
                            break;
                        case "ТипСубъекта":
                            xPathStr = xPathStr + "/ТипСубъекта";
                            break;
                        case "Идентификатор":
                            if(xPathStr.equals("Субъект/ТипСубъекта")){
                                nextEvent = reader.nextEvent();
                                if(Integer.parseInt(nextEvent.asCharacters().getData())==4) {//если субъект - Физ лицо,  создаем объект
                                    subject = new Subject();
                                    subject.setSubjectType(4);
                                    subject.setSubjectNumberInList(Subject_number_in_list);
                                } else if (Integer.parseInt(nextEvent.asCharacters().getData())==3) {
                                    subject = new Subject();
                                    subject.setSubjectType(3);
                                    subject.setSubjectNumberInList(Subject_number_in_list);
                                    subject.setSubjectFio("Организация");
                                }
                            }
                            break;
                        case "ФЛ":
                            xPathStrFL = "ФЛ";
                            break;
                        case "ИНН":
                            if(xPathStrFL.equals("ФЛ")) {
                                nextEvent = reader.nextEvent();
                                subject.setSubjectInn(nextEvent.asCharacters().getData().trim());
                            }
                            break;
                        case "СНИЛС" :
                            if(xPathStrFL.equals("ФЛ")) {
                                nextEvent = reader.nextEvent();
                                subject.setSubjectSnils(nextEvent.asCharacters().getData().trim());
                            }
                            break;
                        case "ФИО" :
                            if(xPathStrFL.equals("ФЛ")) {
                                nextEvent = reader.nextEvent();
                                subject.setSubjectFio(nextEvent.asCharacters().getData().trim());
                            }
                            break;
                        case "Террорист" :
                            nextEvent = reader.nextEvent();
                            subject.setSubjectCategory(nextEvent.asCharacters().getData().trim());
                            break;
                        case "ДатаРождения" :
                            if(xPathStrFL.equals("ФЛ")) {
                                nextEvent = reader.nextEvent();
                                String[] date_parts = nextEvent.asCharacters().getData().trim().substring(0, 10).split("-");   //значение даты из xml в гггг, мм, дд,
                                LocalDate birthday = LocalDate.of(Integer.parseInt(date_parts[0]), Integer.parseInt(date_parts[1]), Integer.parseInt(date_parts[2])); //собираем LocalDate
                                subject.setSubjectBirthday(birthday);
                            }
                            break;

                        case "СписокАдресов":
                            // xPathStr = "СписокАдресов";
                            break;
                        case "СписокДокументов":
                            if(xPathStrFL.equals("ФЛ")) {
                                xPathStrDocType = "СписокДокументов";
                                xPathStrDoc = "СписокДокументов";
                            }
                            break;
                        case "Документ":
                            if(xPathStrFL.equals("ФЛ")) {
                                xPathStrDocType = xPathStrDocType + "/Документ";
                                xPathStrDoc = xPathStrDoc + "/Документ";
                                document = new Document();
                                document.setPerson(subject);
                                if (subject.getDocuments().isEmpty()) {  // у ФЛ нет еще документов
                                    subject.setDocuments(new ArrayList<>(Collections.singletonList(document)));
                                } else {
                                    subject.getDocuments().get().add(document);
                                }
                            }
                            break;
                        case "ТипДокумента":
                            if(xPathStrDocType.equals("СписокДокументов/Документ")) {
                                xPathStrDocType = xPathStrDocType + "/ТипДокумента";
                            }
                            break;
                        case "Наименование":
                            if(xPathStrDocType.equals("СписокДокументов/Документ/ТипДокумента")){
                                nextEvent = reader.nextEvent();
                                document.setDoc_type(nextEvent.asCharacters().getData().trim());
                            }
                            break;
                        case "Серия":
                            if(xPathStrDoc.equals("СписокДокументов/Документ")){
                                nextEvent = reader.nextEvent();
                                document.setDoc_ser(nextEvent.asCharacters().getData().trim());
                            }
                            break;
                        case "Номер":
                            if(xPathStrDoc.equals("СписокДокументов/Документ")){
                                nextEvent = reader.nextEvent();
                                document.setDoc_number(nextEvent.asCharacters().getData().trim());
                            }
                            break;
                        case "ОрганВыдачи":
                            if(xPathStrDoc.equals("СписокДокументов/Документ")){
                                nextEvent = reader.nextEvent();
                                document.setDoc_organ(nextEvent.asCharacters().getData().trim());
                            }
                            break;
                        case "ДатаВыдачи":
                            if(xPathStrDoc.equals("СписокДокументов/Документ")){
                                nextEvent = reader.nextEvent();
                                String[] date_parts2 =nextEvent.asCharacters().getData().trim().substring(0,10).split("-");   //значение даты из xml в гггг, мм, дд,
                                LocalDate doc_date_begin = LocalDate.of(Integer.parseInt(date_parts2[0]),Integer.parseInt(date_parts2[1]),Integer.parseInt(date_parts2[2])); //собираем LocalDate
                                document.setDoc_date_begin(doc_date_begin);
                            }
                            break;
                        default:
                           // System.out.println("ff");
                    }

                }
                if (nextEvent.isEndElement()) {   //закрывающий тэг
                    EndElement endElement = nextEvent.asEndElement();

                    //	if(endElement.getName().getLocalPart().equals("Субъект")) break;
                    switch (endElement.getName().getLocalPart()){
                        case "Субъект" :
                            xPathStr="";
                            subjectsService.save(subject);

//                            Session session =  sessionFactory.getCurrentSession();
//                            try {
//                                session.beginTransaction();
//                                session.persist(subject); //в @Entity настроено каскадировнаие, поэтому документы отделно не сохраняем
//                                session.getTransaction().commit();
//
//                            } finally{
//                                //sessionFactory.close();
//                            }
                            loadcount++;
                            subject=null;
                            document =null;
                            break;
                        case "Идентификатор":
                            if(xPathStr.equals("Субъект/ТипСубъекта")){
                                xPathStr="";
                            }
                            break;
                        case "Наименование":
                            if(xPathStrDocType.equals("СписокДокументов/Документ/ТипДокумента")){
                                xPathStrDocType="";
                            }
                            break;
                        case "СписокАдресов":
                            xPathStr="";
                            break;
                        case "Документ":
                            xPathStrDoc= "СписокДокументов";
                            xPathStrDocType =  "СписокДокументов";
                            break;
                        case "ФЛ":
                            xPathStrFL ="";
                            break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
           // sessionFactory.close();
        }

    }


}
