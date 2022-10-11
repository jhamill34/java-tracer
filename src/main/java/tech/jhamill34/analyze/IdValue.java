package tech.jhamill34.analyze;

import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Value;
import tech.jhamill34.entities.Entity;
import tech.jhamill34.entities.EntityVisitor;

import java.util.concurrent.atomic.AtomicInteger;

public class IdValue implements Value, Entity {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    public final BasicValue delegate;
    public final int id;

    private IdValue(BasicValue delegate) {
        this(ID_GENERATOR.getAndIncrement(), delegate);
    }

    private IdValue(int id, BasicValue delegate) {
        this.delegate = delegate;
        this.id = id;
    }

    public static IdValue from(BasicValue value) {
        return new IdValue(value);
    }

    public static IdValue from(int id, BasicValue value) {
        return new IdValue(id, value);
    }

    @Override
    public int getSize() {
        return this.delegate.getSize();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            if (obj instanceof IdValue) {
                IdValue other = (IdValue) obj;
                return this.id == other.id;
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public <T> T accept(EntityVisitor<T> entityVisitor) {
        return entityVisitor.visitValue(this);
    }
}
