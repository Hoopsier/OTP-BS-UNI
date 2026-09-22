pipeline {
    agent any
    tools {
        jdk 'jdk21'
        maven 'Maven3'
    }
        environment{
            DOCKERHUB_CREDENTIALS_ID = 'docker_hub'
            DOCKERHUB_REPO = 'renanhoruz/otp_bs'
            DOCKER_IMAGE_TAG = "v1"
        }
        
    stages {
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
        stage('Build Docker Image'){
          steps{
            script{
              docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
            }
          }
        }
        stage('Push Docker Image to Docker Hub'){
            steps{
                script{
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID){
                        docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                      }
                  }
              }
          }
    }
}
