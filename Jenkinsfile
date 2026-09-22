pipeline {
    agent any
    tools {
        jdk 'jdk21'
        maven 'Maven3'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scmGit(
    branches: [[name: 'main']],
    userRemoteConfigs: [[url: 'https://github.com/Hoopsier/OTP-BS-UNI.git']])
            }
        }
        stage('Compile') {
            steps {
                sh "mvn -f Junit clean install compile package"
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -f Junit clean test'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploy stage completed'
            }
        }
        stage('Code Coverage') {
            steps {
                sh 'mvn -f Junit jacoco:report' // jacoco:report just doesn't work automatically, I got it to work with external settings, that I could not reach on jenkins.
            }
        }
        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }
}
