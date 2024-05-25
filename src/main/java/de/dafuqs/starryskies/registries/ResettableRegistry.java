package de.dafuqs.starryskies.registries;

import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

// daf, please reconsider registrification
public class ResettableRegistry<T> extends SimpleRegistry<T> {
    public ResettableRegistry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
        super(key, lifecycle);
    }

    public void reset() {
        this.rawIdToEntry.clear();
        this.entryToRawId.clear();
        this.idToEntry.clear();
        this.keyToEntry.clear();
        this.valueToEntry.clear();
        this.keyToEntryInfo.clear();
        this.tagToEntryList.clear();
        this.frozen = false;
    }
}
