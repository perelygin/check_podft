package ptv.education.check_podft.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ptv.education.check_podft.dto.CheckPodftPersonRequestDTO;
//import ptv.education.check_podft.dto.SubjectDTO;
//import ptv.education.check_podft.model.Document;
import ptv.education.check_podft.model.Subject;
import ptv.education.check_podft.services.SubjectsService;
import ptv.education.check_podft.util.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PodftRESTController {

    private final SubjectsService subjectsService;
    private final ModelMapper modelMapper;
    @Autowired
    public PodftRESTController(SubjectsService subjectsService,ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.subjectsService = subjectsService;
    }
/*

 */
    @Tag(name = "Проверка потенциального клиента по перечню ПОДФТ", description = "На вход получаем json  с ИНН,ФИО,Дата рождения,СНИЛС. На выходе имеем три логических поля с резульатами проверки по:\n" +
            "1. ИНН, 2. СНИЛС, 3. ФИО+ дата рождения")
    @PostMapping(path = "/checkPODFTPerson")
    public ResponseEntity<CheckPodftPersonResponse> checkPODFTPerson(@RequestBody @Valid CheckPodftPersonRequestDTO checkPodftPersonRequestDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){  // есть ошибки валидации
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors =  bindingResult.getFieldErrors();
            for (FieldError error: errors){
                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");
            }
            throw new checkPODFTPersonExeption(errorMessage.toString());   // генерируем исключение правильное
        }
        // ищем по ИНН,SNILS,ФИО+ДР.
        Optional<Subject> subjectInn = subjectsService.findBySubjectInn(checkPodftPersonRequestDTO.getSubjectInn());
        Optional<Subject> subjectSnils = subjectsService.findBySubjectSnils(checkPodftPersonRequestDTO.getSubjectSnils());
        Optional<Subject> subjectFioB = subjectsService.findBySubjectFioAndSubjectBirthday(checkPodftPersonRequestDTO.getSubjectFio(), checkPodftPersonRequestDTO.getSubjectBirthday());
        //готовим ответ
        CheckPodftPersonResponse checkPodftPersonResponse =  new CheckPodftPersonResponse( subjectInn.isPresent(),
                                                                                           subjectSnils.isPresent(),
                                                                                           subjectFioB.isPresent());
        //отправляем HTTP ответ со статусом 200  и телом ответа
        return new ResponseEntity<>(checkPodftPersonResponse, HttpStatus.OK);
    }

//    @GetMapping("/check_fio")
//    public  List<Subject>  check_fio(){
//        List<Subject> s1 = subjectsService.findBySubjectFio("ИГНАТЕНКО ВЕРА ИЛЬИНИЧНА");
//        for(Subject s:s1) {
//            System.out.println(s.getSubjectFio()+" "+s.getSubjectInn());
//            if(s.getDocuments().isPresent()) {
//                for (Document doc :s.getDocuments().get()) {
//                    System.out.println(doc.getDoc_number());
//                }
//            }
//        }
//        return s1;
//    }
//    @GetMapping("/getsubject/{id}")
//    public  SubjectDTO  get_subject(@PathVariable("id") int id){
//        List<Subject> s1 = subjectsService.findBySubjectFio("ИГНАТЕНКО ВЕРА ИЛЬИНИЧНА");
//        System.out.println(id);
//        return convertSubjectToDTO(s1.get(0));
//    }

//    @GetMapping("/findbyinn/{inn}")
//    public  Optional<Subject>  findByInn(@PathVariable("inn") String inn){
//        System.out.println(inn);
//        return subjectsService.findBySubjectInn(inn);
//    }
//
//    @PostMapping(path = "/createSubjectDTO")
//    public ResponseEntity<HttpStatus> createDTO(@RequestBody @Valid SubjectDTO subjectDTO, BindingResult bindingResult){
//        if(bindingResult.hasErrors()){  // есть ошибки валидации
//            StringBuilder errorMessage = new StringBuilder();
//            List<FieldError> errors =  bindingResult.getFieldErrors();
//            for (FieldError error: errors){
//                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");
//            }
//            throw new SubjectNotCreatedExeption(errorMessage.toString());   //генерируем исключение
//        }
//        subjectsService.save(convertDTOToMapper(subjectDTO));
//        //отправляем HTTP ответ с путсым телом и статусом 200
//        return ResponseEntity.ok(HttpStatus.OK);
//    }
//
//    private Subject convertDTO(SubjectDTO subjectDTO) {
//        Subject subject = new Subject();
//        subject.setSubjectFio(subjectDTO.getSubjectFio());
//        subject.setSubjectInn(subjectDTO.getSubjectInn());
//        subject.setSubjectBirthday(subjectDTO.getSubjectBirthday());
//        subject.setSubjectSnils(subject.getSubjectSnils());
//
//        return subject;
//    }
//    private Subject convertDTOToMapper(SubjectDTO subjectDTO) {
//      //  ModelMapper modelMapper = new ModelMapper();
//        Subject subject = modelMapper.map(subjectDTO, Subject.class);
//        //проставляем связь между субьектом и документами в документах.
//        Optional<List<Document>> documents = subject.getDocuments();
//        if (documents.isPresent()){
//            for(Document doc: documents.get()){
//              doc.setPerson(subject);
//            }
//        }
//        System.out.println(subject);
//        //List<DocumentDTO> documentDTOList = modelMapper.map(subjectDTO, Subject.class);
//        return subject;
//    }
//    private SubjectDTO convertSubjectToDTO(Subject subject) {
//        //  ModelMapper modelMapper = new ModelMapper();
//        SubjectDTO subjectDTO = modelMapper.map(subject, SubjectDTO.class);
//
//        return subjectDTO;
//    }


//    @PostMapping(path = "/createSubject", consumes = "application/json", produces = "application/json")
//   public ResponseEntity<HttpStatus> create(@RequestBody @Valid Subject subject, BindingResult bindingResult){
//        if(bindingResult.hasErrors()){  // есть ошибки валидации
//            StringBuilder errorMessage = new StringBuilder();
//            List<FieldError> errors =  bindingResult.getFieldErrors();
//            for (FieldError error: errors){
//                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");//
//            }
//            throw new SubjectNotCreatedExeption(errorMessage.toString());   //генерируем исключение
//        }
//        subjectsService.save(subject);
//        //отправляем HTTP ответ с путсым телом и статусом 200
//        return ResponseEntity.ok(HttpStatus.OK);
//   }
//
//    @ExceptionHandler
//    private ResponseEntity<SubjectErrorResponse> handleExeption(SubjectNotFoundExeption e){
//        SubjectErrorResponse subjectErrorResponse = new SubjectErrorResponse("Subject wasn't found",System.currentTimeMillis());
//        return new ResponseEntity<>(subjectErrorResponse, HttpStatus.NOT_FOUND); // NOT_FOUND - 404й код ответа
//    }
//    @ExceptionHandler
//    private ResponseEntity<SubjectErrorResponse> handleExeption(SubjectNotCreatedExeption e){
//        SubjectErrorResponse subjectErrorResponse = new SubjectErrorResponse(e.getMessage(), System.currentTimeMillis());  //  e.getMessage() - это  то , что мы положили при иницировании исключения errorMessage.toString()
//        return new ResponseEntity<>(subjectErrorResponse, HttpStatus.BAD_REQUEST); // NOT_FOUND - 404й код ответа
//    }


    @ExceptionHandler
    private ResponseEntity<SubjectErrorResponse> handleExeption(checkPODFTPersonExeption e){
        SubjectErrorResponse subjectErrorResponse = new SubjectErrorResponse(e.getMessage(), System.currentTimeMillis());  //  e.getMessage() - это  то , что мы положили при иницировании исключения errorMessage.toString()
        return new ResponseEntity<>(subjectErrorResponse, HttpStatus.BAD_REQUEST); //
    }
}
