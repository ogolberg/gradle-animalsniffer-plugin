package ru.vyarus.gradle.plugin.animalsniffer.signature

import org.gradle.api.Project
import ru.vyarus.gradle.plugin.animalsniffer.AbstractTest
import ru.vyarus.gradle.plugin.animalsniffer.AnimalSniffer

/**
 * @author Vyacheslav Rusakov
 * @since 24.04.2017
 */
class BuildSignatureTaskTest extends AbstractTest {

    def "Check no default build task registration"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"
        }

        then: "tasks registered"
        project.tasks.withType(AnimalSniffer).size() == 2
        project.tasks.withType(BuildSignatureTask).size() == 2  // disabled cache tasks
        project.tasks.withType(BuildSignatureTask).find { it.enabled } == null
    }


    def "Check default build task registration"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            animalsnifferSignature {
                files sourceSets.main.output
            }
        }

        then: "tasks registered"
        project.tasks.withType(AnimalSniffer).size() == 2
        project.tasks.withType(BuildSignatureTask).size() == 3  // +2 disabled cache tasks
        project.tasks.withType(BuildSignatureTask).findAll { it.enabled }.size() == 1

        then: "defaults correct"
        BuildSignatureTask task = project.tasks.animalsnifferSignature
        task.outputDirectory.canonicalPath.replace('\\', '/').endsWith("build/animalsniffer/signature")
        task.outputName == project.name
        task.animalsnifferClasspath != null
    }

    def "Check custom task defaults"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            tasks.create('sig', ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask) {
                files sourceSets.main.output
            }
        }

        then: "defaults correct"
        BuildSignatureTask task = project.tasks.sig
        task.outputDirectory.canonicalPath.replace('\\', '/').endsWith("build/animalsniffer/sig")
        task.outputName == 'sig'
        task.animalsnifferClasspath != null
    }
}
