package ptv.education.check_podft.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import ptv.education.check_podft.model.Subject;
import ptv.education.check_podft.repositories.DocumentsRepository;

@Service
@Transactional(readOnly = true)
public class DocumentsService {
    private final DocumentsRepository documentsRepository;

    @Autowired
    public DocumentsService(DocumentsRepository documentsRepository) {
        this.documentsRepository = documentsRepository;
    }

    @Transactional
    public void truncateDocuments(){
        documentsRepository.truncateDocuments();
        System.out.println("Docum truncated");
    }
}
