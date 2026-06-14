pipeline {
    agent any

    stages {

        stage('PULL STAGE 2') {
            steps {
                git 'https://github.com/prachiambadkar2001-collab/Jenkins_pipeline.git'
            }
        }

        stage('FRONTEND-DOCKER-BUILD') {
            steps {
                sh '''
                cd frontend
                docker build -t prachiambadkar2001-collab/easy-frontend:latest .
                '''
            }
        }

        stage('BACKEND-DOCKER-BUILD') {
            steps {
                sh '''
                cd backend
                docker build -t prachiambadkar2001-collab/easy-backend:latest .
                '''
            }
        }

        stage('DOCKER-PUSH') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                    echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                    docker push prachiambadkar2001-collab/easy-frontend:latest
                    docker push prachiambadkar2001-collab/easy-backend:latest

                    docker logout
                    '''
                }
            }
        }

        stage('DOCKER-CLEAN') {
            steps {
                sh '''
                docker rmi -f prachiambadkar2001-collab/easy-frontend:latest || true
                docker rmi -f prachiambadkar2001-collab/easy-backend:latest || true
                '''
            }
        }

        stage('DEPLOY') {
           steps {
               sh '''
        aws eks update-kubeconfig \
          --region us-east-1 \
          --name my-cluster

        kubectl get nodes

        kubectl apply -f simple-deploy/ '''
           }
        }
    }
    
}
