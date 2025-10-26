pipeline {
    agent any
    tools {
        dockerTool 'docker' // 'docker' (Tools에서 설정한 이름)
    }

    environment {
        // 1. Docker Hub ID와 백엔드 이미지 이름
        DOCKER_IMAGE_NAME = "cjy951213/ebear-restapi-image"
        
        // 2. Docker Hub 인증서 ID
        DOCKER_CREDENTIAL_ID = "dockerhub-credentials"
        
        // 3. 원격 서버 SSH 인증서 ID
        REMOTE_SSH_CREDENTIAL_ID = "remote-server-ssh"
        
        // 4. 원격 서버 접속 정보 (ID@IP주소)
        REMOTE_SERVER = "cjy951213@cjy951213.iptime.org"
        
        // 5. 원격 서버 SSH 포트
        REMOTE_PORT = "40022"
    }

    stages {
        stage('Prepare (Gradle)') {
            steps {
                // EBearRestApi 폴더로 이동해서 clean 실행
                sh 'cd EBearRestApi && ./gradlew clean'
            }
        }

        stage('Build (Gradle)') {
            steps {
                // EBearRestApi 폴더로 이동해서 build 실행
                sh 'cd EBearRestApi && ./gradlew build'
            }
        }

        stage('Build & Login (Docker)') {
            steps {
                script {
                    echo "--- 1. Docker Hub 로그인 ---"
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIAL_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                    }

                    echo "--- 2. Docker 이미지 빌드 ---"
                    // Docker Hub에 올릴 이름으로 빌드 (Dockerfile 위치: EBearRestApi)
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:latest EBearRestApi"
                }
            }
        }

        stage('Push Image to Docker Hub') {
            steps {
                echo "--- 3. Docker Hub로 이미지 푸시 ---"
                sh "docker push ${DOCKER_IMAGE_NAME}:latest"
            }
        }

        stage('Deploy to Remote Server') {
            steps {
                echo "--- 4. 원격 서버에 SSH 접속 및 배포 ---"
                sshagent(credentials: [REMOTE_SSH_CREDENTIAL_ID]) {
                    sh """
                        ssh -p ${REMOTE_PORT} -o StrictHostKeyChecking=no ${REMOTE_SERVER} '
                        
                        echo "--- (원격) 1. 최신 이미지 PULL ---"
                        docker pull ${DOCKER_IMAGE_NAME}:latest
                        
                        echo "--- (원격) 2. 기존 컨테이너 중지 및 삭제 ---"
                        docker stop ebear-restapi-app || true
                        docker rm ebear-restapi-app || true
                        
                        echo "--- (원격) 3. 새 컨테이너 실행 ---"
                        docker run -d -e TZ=Asia/Seoul -p 8888:8888 --name ebear-restapi-app ${DOCKER_IMAGE_NAME}:latest
                        
                        echo "--- (원격) 배포 완료 ---"
                        '
                    """
                }
            }
        }
    }
    
    post {
        // 빌드 성공/실패 여부와 관계없이 항상 로그아웃
        always {
            echo "--- 5. Docker Hub 로그아웃 ---"
            sh "docker logout"
        }
    }
}