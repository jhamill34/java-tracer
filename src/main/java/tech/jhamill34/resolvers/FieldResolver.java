package tech.jhamill34.resolvers;

import com.google.inject.Inject;
import org.objectweb.asm.Type;
import tech.jhamill34.entities.ClassEntity;
import tech.jhamill34.entities.FieldEntity;
import tech.jhamill34.tree.ClassRepository;

public class FieldResolver {
    @Inject
    private ClassRepository classRepository;

    public ClassEntity getOwner(FieldEntity fieldEntity) {
        return classRepository.findById(fieldEntity.getOwnerId());
    }

    public String getType(FieldEntity fieldEntity) {
        return Type.getType(fieldEntity.getDescriptor()).getClassName();
    }
}
