package tech.jhamill34.analyze.handlers;

import com.google.inject.Inject;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicValue;
import tech.jhamill34.analyze.ArrayHandler;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.analyze.IdValue;

public class ArrayHandlerImpl implements ArrayHandler {
    @Inject
    private HeapStore heapStore;

    @Override
    public void alloc(IdValue ref) {
        String arrayType = ref.delegate.getType().getInternalName();
        if (arrayType.charAt(0) != '[') {
            throw new RuntimeException("Unexpected type in array alloc");
        }

        String type = arrayType.substring(1);
        BasicValue delegate = new BasicValue(Type.getType(type));
        IdValue item = IdValue.from(delegate);
        heapStore.createArray(ref, item);

        if (type.charAt(0) == '[') {
            alloc(item);
        }
    }

    @Override
    public void store(IdValue ref, IdValue value) {
        alloc(ref);
        heapStore.storeArray(ref, value);
    }

    @Override
    public IdValue load(IdValue ref, IdValue defaultValue) {
        alloc(ref);
        return heapStore.loadArray(ref);
    }
}
