package tech.jhamill34.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import tech.jhamill34.analyze.HeapStore;
import tech.jhamill34.repos.InMemoryClassRepo;
import tech.jhamill34.repos.InMemoryFieldRepo;
import tech.jhamill34.repos.InMemoryInstructionRepo;
import tech.jhamill34.repos.InMemoryMethodRepo;
import tech.jhamill34.heap.InMemoryHeapStore;
import tech.jhamill34.tree.ClassRepository;
import tech.jhamill34.tree.FieldRepository;
import tech.jhamill34.tree.InstructionRepository;
import tech.jhamill34.tree.MethodRepository;

public class RepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ClassRepository.class).to(InMemoryClassRepo.class).in(Singleton.class);
        bind(MethodRepository.class).to(InMemoryMethodRepo.class).in(Singleton.class);
        bind(InstructionRepository.class).to(InMemoryInstructionRepo.class).in(Singleton.class);
        bind(FieldRepository.class).to(InMemoryFieldRepo.class).in(Singleton.class);

        bind(HeapStore.class).to(InMemoryHeapStore.class).in(Singleton.class);
    }
}
