pipeline {
    agent any
    tools {
        jdk 'jdk21'
        maven 'Maven3'
    }
        environment{
            DOCKERHUB_CREDENTIALS_ID = 'docker_hub'
            DOCKERHUB_REPO = 'renanhoruz/otp_bs'
            DOCKER_IMAGE_TAG = "latest"
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
              sh  '''
                    docker build -t ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG} \
                    -t ${DOCKERHUB_REPO}:latest \
                    Junit
                  '''
            }
          }
        }
        stage('Push Docker Image to Docker Hub'){
            steps{
                script{
                  withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        docker login -u &{DOCKER_USER} -p &{DOCKER_PASS}
                        docker push &{DOCKERHUB_REPO%:&{DOCKER_IMAGE_TAG}
                    '''
                  }
                }
              }
          }
    }
}
