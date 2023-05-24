package com.ryandens.temurin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings
import java.net.URI

class TemurinBinariesRepository : Plugin<Any> {

    companion object {
        internal val REPO_REGEX = "^temurin([1-9]*)-binaries"
    }
    override fun apply(target: Any) {
        if (target is Project) {
            apply(target)
        } else if (target is Settings) {
            apply(target)
        }
    }

    private fun apply(project: Project) = project.repositories.addRepository()

    private fun apply(settings: Settings) = settings.dependencyResolutionManagement.repositories.addRepository()

    private fun RepositoryHandler.addRepository() {
        exclusiveContent { exclusiveContentRepository ->
            exclusiveContentRepository.forRepository {
                ivy { ivyRepository ->
                    ivyRepository.url = URI.create("https://github.com/adoptium/")
                    ivyRepository.patternLayout { layout ->
                        layout.artifact("[organisation]/releases/download/[revision]/[module].[ext]")
                    }
                    ivyRepository.metadataSources { metadataSources ->
                        metadataSources.artifact()
                    }
                }
            }
            exclusiveContentRepository.filter { filter ->
                filter.includeGroupByRegex(REPO_REGEX)
            }
        }
    }
}
