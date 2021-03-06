package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 06.07.2017
 */
class AndroidValidationKitTest extends AbstractKitTest {

    def "Check multiple signatures"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral() }
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
            }
        """
        fileFromClasspath('src/main/java/android/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/android/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.output.contains("3 AnimalSniffer violations were found in 1 files")
        result.output.contains("[Undefined reference (java16-sun-1.0)]")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                'android.Sample:9  Undefined reference (java16-sun-1.0): int Boolean.compare(boolean, boolean)',
                'android.Sample:9  Undefined reference (android-api-level-14-4.0_r4): int Boolean.compare(boolean, boolean)',
                'android.Sample:14  Undefined reference (android-api-level-14-4.0_r4): Object javax.naming.InitialContext.doLookup(String)'
        ]
    }
}
