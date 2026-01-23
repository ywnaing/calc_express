package dev.yewintnaing.expreval.storage;

import dev.yewintnaing.expreval.core.Operand;
import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for storing and retrieving operands used in expressions.
 */
public interface OperandStorage {

    /**
     * Adds an operand to the storage.
     *
     * @param operand The operand to add
     */
    void add(Operand operand);

    /**
     * Retrieves an operand by its key.
     *
     * @param key The key of the operand
     * @return An Optional containing the operand if found, or empty otherwise
     */
    Optional<Operand> get(String key);

    /**
     * Retrieves all stored operands.
     *
     * @return A list of all operands
     */
    List<Operand> getAll();

    /**
     * Checks if an operand exists for the given key.
     *
     * @param key The key to check
     * @return true if it exists, false otherwise
     */
    boolean contains(String key);

    /**
     * Removes an operand by its key.
     *
     * @param key The key of the operand to remove
     */
    void remove(String key);

    /**
     * Clears all operands from the storage.
     */
    void clear();
}
