// The library version is controled from the Jenkins configuration
// To force a version add after lib '@' followed by the version.
@Library('msaas-shared-lib') _

def ibpMavenSettingsFileCredential = 'ibp-maven-settings-file'

node {
  // setup the global static configuration
  config = setupMsaasPipeline('msaas-config.yaml')
}

pipeline {
   agent {
      kubernetes {
         label "mypod-${UUID.randomUUID().toString()}"
         defaultContainer "maven"
                    yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: docker.intuit.com/docker-rmt/maven:3.5.3-jdk-8
    command:
    - cat
    tty: true
  - name: docker
    image: docker.intuit.com/docker-rmt/docker:18.09.1
    tty: true
    command: [ "cat" ]
    volumeMounts:
    - name: dind-volume
      mountPath: /var/run/docker.sock
  volumes:
  - name: dind-volume
    hostPath:
      path: /var/run/dind/docker.sock
"""
        }
    }

   environment {
      IBP_MAVEN_SETTINGS_FILE = credentials("${ibpMavenSettingsFileCredential}")
   }

   stages {
      stage('master build') {
            when { allOf { branch 'master'; not {changeRequest()} } }
               steps {
                  container('maven') {
                     echo "Running Build for master branch"
                     sh "./build-release.sh"
                     sh "mvn -s ${IBP_MAVEN_SETTINGS_FILE} clean deploy"
                  }
               }
               post {
                   always {
                        jacoco exclusionPattern: "**/*Test*", inclusionPattern: "**/*.class,**/*.java"
                   }
               }
            }
   }
}
