pipeline {
    agent any
    tools {
        jdk 'jdk21'
        maven 'Maven3'
    }
        environment{
            DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
            DOCKERHUB_REPO = 'renanhoruz/otp_bs'
            DOCKER_IMAGE_TAG = "latest"

            DB_PORT = '3306'
            DB_NAME = 'temp_temperature'
            DB_USER = 'hoopsy'
            DB_PASSWORD = '123123'
        }
        
    stages {
      stage('Checkout') {
            steps {
                checkout scm
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
        stage('Build Docker Image'){
          steps{
            script{
              sh '''
                    docker rm -f otp-bs-app || true

                    docker run -d \
                        --name otp-bs-app \
                        --network host \
                        -e DB_HOST=127.0.0.1 \
                        -e DB_PORT=${DB_PORT} \
                        -e DB_NAME=${DB_NAME} \
                        -e DB_USER=${DB_USER} \
                        -e DB_PASSWORD=${DB_PASSWORD} \
                        -e DISPLAY=$DISPLAY \
                        -v /tmp/.X11-unix:/tmp/.X11-unix \
                        ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}
                '''
            }
          }
        }
        
        stage('Login to Docker Hub') {
          steps {
              withCredentials([
                  usernamePassword(
                      credentialsId: 'dockerhub-credentials',
                      usernameVariable: 'DOCKER_USER',
                      passwordVariable: 'DOCKER_PASS'
                  )
              ]) {
                  sh '''
                      echo "$DOCKER_PASS" | docker login \
                        -u "$DOCKER_USER" \
                        --password-stdin
                  '''
              }
          }
        }

        stage('Push Docker Image') {
            steps {
                sh '''
                    docker push ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}
                    docker push ${DOCKERHUB_REPO}:latest
                   '''
            }
        }
    }
  post {
    always {
        sh 'docker logout || true'
    }
  }
}
