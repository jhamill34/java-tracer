package tech.jhamill34.resolvers;

import com.google.inject.Inject;
import org.objectweb.asm.Type;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.entities.EntityUtilities;
import tech.jhamill34.entities.FieldEntity;
import tech.jhamill34.tree.ClassRepository;

import java.util.List;

public class FieldResolver {
    @Inject
    private ClassRepository classRepository;

    public ClassEntity getOwner(FieldEntity fieldEntity) {
        return classRepository.findById(fieldEntity.getOwnerId());
    }

    public String getType(FieldEntity fieldEntity) {
        return EntityUtilities.convertFieldType(fieldEntity);
    }

    public List<String> getAccessList(FieldEntity fieldEntity) {
        return EntityUtilities.convertAccessList(fieldEntity.getAccess());
    }
}
