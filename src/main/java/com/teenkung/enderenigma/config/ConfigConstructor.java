package com.teenkung.enderenigma.config;

import com.teenkung.enderenigma.EnderEnigma;
import org.bukkit.configuration.file.FileConfiguration;


/**
 * Interface for ConfigLoader class
 * @param <T> Variable
 */
@FunctionalInterface
public interface ConfigConstructor<T> {
    T construct(FileConfiguration config, EnderEnigma plugin);
}