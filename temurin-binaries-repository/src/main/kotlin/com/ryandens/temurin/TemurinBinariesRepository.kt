package com.ryandens.temurin

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class TemurinBinariesRepository : Plugin<Project> {
    override fun apply(project: Project) {
        project.repositories.apply {
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
                    filter.includeGroupByRegex("^temurin([1-9]*)-binaries")
                }
            }
        }
    }
}
