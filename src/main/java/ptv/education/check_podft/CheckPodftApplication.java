package ptv.education.check_podft;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ptv.education.check_podft.model.Document;
import ptv.education.check_podft.model.ListInfo;
import ptv.education.check_podft.model.Subject;

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
import java.util.List;

@SpringBootApplication
public class CheckPodftApplication {

	public static void main(String[] args) {

		String path = "C:\\tmp\\18.12.2024-v 2.1.xml";
		//load_xml(path);
		/// Запуск приложения
		SpringApplication.run(CheckPodftApplication.class, args);

	}
	@Bean
	public ModelMapper modelMapper(){
			return  new ModelMapper();
	}
	public static void load_xml(String path){
		boolean need_reload = true;   // если дата нового перечня больше даты последнего загруженного, то нужно загружать новый перечень. Перед этим удаляем данные из таблиц.
		Subject subject = null;  //ФЛ или ЮЛ
		Document document = null;
		String formatVersion="";
		String xPathStr = "";  // строка с содержит Xpath до элемента.   <Идентификатор> Для типа субьекта
		String xPathStrDocType = "";  // строка с содержит Xpath до элемента. <наименование>.Для типа документов
		String xPathStrDoc = "";  // строка с содержит Xpath до элементов <Документ>.Для документов
		String xPathStrFL ="";  // строка с содержит Xpath до элементов  ФЛ
		int Subject_number_in_list = 0;   // ИдСубъекта 	 находится в фАЙЛЕ до типа субъекта.  и если  субъект - ЮЛ, то  объект не будет создан и мы не сможем сделать setSubject_number_in_list()

		int loadcount =0;
		/*
		подключаем Hibernate
		 */
		Configuration configuration = new Configuration().addAnnotatedClass(Subject.class).addAnnotatedClass(Document.class).addAnnotatedClass(ListInfo.class);
		SessionFactory sessionFactory = configuration.buildSessionFactory();

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
							Session session =  sessionFactory.getCurrentSession();
							try {
								session.beginTransaction();
								List<ListInfo> listInfos = session.createQuery("FROM ListInfo linf ORDER BY linf.listDate DESC LIMIT 1").getResultList();
								if(!listInfos.isEmpty()){
									LocalDate saved_date =listInfos.get(0).getListDate();
									String[] date_parts1 =nextEvent.asCharacters().getData().trim().substring(0,10).split("-");   //значение даты из xml в гггг, мм, дд,
									LocalDate new_date = LocalDate.of(Integer.parseInt(date_parts1[0]),Integer.parseInt(date_parts1[1]),Integer.parseInt(date_parts1[2])); //собираем LocalDate
									if(new_date.isBefore(saved_date) || new_date.isEqual(saved_date) ){   //если дата из xml раньше   или равна сохраненной, то грузить список не нужно.
										need_reload=false;
									} else {  //иначе записываем в  базу информацию о новом списке  и чистим базу
										ListInfo listInfo =  new ListInfo(formatVersion,new_date,saved_date);
										session.persist(listInfo);
										//vsubjects session.createQuery("delete from subjects").executeUpdate();
										session.createNativeQuery("DELETE FROM podft.subjects").executeUpdate();
									}
								}
								session.getTransaction().commit();
							} finally{
								//	sessionFactory.close();
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
								String[] date_parts1 =nextEvent.asCharacters().getData().trim().substring(0,10).split("-");   //значение даты из xml в гггг, мм, дд,
								LocalDate doc_date_begin = LocalDate.of(Integer.parseInt(date_parts1[0]),Integer.parseInt(date_parts1[1]),Integer.parseInt(date_parts1[2])); //собираем LocalDate
								document.setDoc_date_begin(doc_date_begin);
							}
							break;
						default:

					}

				}
				if (nextEvent.isEndElement()) {   //закрывающий тэг
					EndElement endElement = nextEvent.asEndElement();

					//	if(endElement.getName().getLocalPart().equals("Субъект")) break;
					switch (endElement.getName().getLocalPart()){
						case "Субъект" :
							xPathStr="";

							Session session =  sessionFactory.getCurrentSession();
							try {
								session.beginTransaction();
								session.persist(subject); //в @Entity настроено каскадировнаие, поэтому документы отделно не сохраняем
								session.getTransaction().commit();

							} finally{
								//sessionFactory.close();
							}
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
			sessionFactory.close();
		}

	}

}
