package com.ryandens.temurin

import kotlin.test.Test
import kotlin.test.assertTrue

class TemurinBinariesRepositoryTest {

    @Test fun `plugin registers task`() {
        // Create a test project and apply the plugin
        assertTrue(TemurinBinariesRepository.REPO_REGEX.toRegex().matches("temurin8-binaries"))
        assertTrue(TemurinBinariesRepository.REPO_REGEX.toRegex().matches("temurin11-binaries"))
        assertTrue(TemurinBinariesRepository.REPO_REGEX.toRegex().matches("temurin17-binaries"))
        assertTrue(TemurinBinariesRepository.REPO_REGEX.toRegex().matches("temurin19-binaries"))
        assertTrue(TemurinBinariesRepository.REPO_REGEX.toRegex().matches("temurin20-binaries"))
        assertTrue(TemurinBinariesRepository.REPO_REGEX.toRegex().matches("temurin21-binaries"))
        assertTrue(TemurinBinariesRepository.REPO_REGEX.toRegex().matches("temurin22-binaries"))
        assertTrue(TemurinBinariesRepository.REPO_REGEX.toRegex().matches("temurin30-binaries"))
    }
}
