package ptv.education.check_podft.services;

//import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptv.education.check_podft.model.ListInfo;
//import ptv.education.check_podft.model.Subject;
import ptv.education.check_podft.repositories.ListInfoRepository;

//import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ListInfoService {
    private final ListInfoRepository listInfoRepository;

    public ListInfoService(ListInfoRepository listInfoRepository) {
        this.listInfoRepository = listInfoRepository;
    }
    public ListInfo findOne(int id){
        Optional<ListInfo> foundList = listInfoRepository.findById(id);
        return  foundList.orElse(null);
    }

    public Optional<ListInfo>  findLastListDate(){
        return Optional.ofNullable(listInfoRepository.findLastListDate());
    }

    @Transactional
    public void save(ListInfo listInfo ){
        listInfoRepository.save(listInfo);
    }

}
