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
              sh  '''
                    docker build -t ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG} \
                    -t ${DOCKERHUB_REPO}:latest \
                    Junit
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
