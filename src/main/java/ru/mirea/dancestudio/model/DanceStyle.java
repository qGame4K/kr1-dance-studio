package ru.mirea.dancestudio.model;

/**
 * Направление танца, которое преподают в студии.
 * Это перечисление (enum) — фиксированный список допустимых значений,
 * чтобы в базе не могли появиться произвольные опечатки вроде "хипхоп" и "HipHop".
 */
public enum DanceStyle {
    HIP_HOP,
    SALSA,
    BACHATA,
    BALLET,
    CONTEMPORARY,
    LATINA,
    WALTZ,
    JAZZ_FUNK
}
