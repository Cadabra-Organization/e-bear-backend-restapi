pipeline{
    agent any
    stages{
       stage('Prepare'){
            steps {
                sh 'cd EBearRestApi && ./gradlew clean'
            }
        }
        stage('Build') {
            steps {
                sh 'cd EBearRestApi && ./gradlew build'
            }
        }
        stage('Deploy Prepare Check Running Container'){
            steps{
                 script {
                    // Docker 컨테이너의 이름 또는 ID
                    def containerName = 'ebear-restapi-app'

                    // Docker 컨테이너가 가동 중인지 확인
                    def result = sh(script: "docker ps -q -f name=${containerName}", returnStdout: true).trim()

                    if (result) {
                        sh "docker stop ${containerName}"
                    }
                }
            }
        }
        stage('Deploy Prepare Check Container') {
            steps {
                script {
                    // Docker 컨테이너의 이름 또는 ID
                    def containerName = 'ebear-restapi-app'

                    // 모든 Docker 컨테이너 목록을 가져와서 해당 컨테이너가 존재하는지 확인
                    def result = sh(script: "docker ps -a -q -f name=${containerName}", returnStdout: true).trim()

                    if (result) {
                        sh "docker rm -f ${containerName}"
                    }
                }
            }
        }
        stage('Deploy Prepare Check Image') {
            steps {
                script {
                    // 확인할 Docker 이미지의 이름과 태그
                    def imageName = 'ebear-restapi-image'
                    def imageId

                    // Docker 이미지의 전체 이름 (이름과 태그)
                    def imageFullName = "${imageName}"

                    // Docker 이미지가 존재하는지 확인
                    imageId = sh(script: "docker images -q ${imageFullName}", returnStdout: true).trim()

                    if (imageId) {
                        sh "docker rmi ${imageId}"
                    }
                }
            }
        }
        stage('Deploy Prepare Make Image') {
            steps {
                script {
                    // Dockerfile이 위치한 디렉토리 (Dockerfile 경로)
                    def dockerfileDir = 'EBearRestApi'
                    // 빌드할 Docker 이미지의 이름과 태그
                    def imageName = 'ebear-restapi-image'
                    def imageFullName = "${imageName}"

                    // Docker 이미지 빌드
                    echo "Building Docker image '${imageFullName}' from Dockerfile located at '${dockerfileDir}'..."
                    sh "docker build -t ${imageFullName} ${dockerfileDir}"

                    // 빌드 결과 확인
                    echo "Docker image '${imageFullName}' built successfully."
                }
            }
        }
        stage('Deploy Prepare Make&Run Container') {
            steps {
                script {
                   // 컨테이너의 이름
                    def containerName = 'ebear-restapi-app'
                    // Docker 이미지 이름과 태그
                    def imageName = 'ebear-restapi-image'
                    def imageFullName = "${imageName}"

                    // Docker 컨테이너를 실행하기 전에 컨테이너가 이미 존재하는지 확인하고 삭제
                    def existingContainerId = sh(script: "docker ps -a -q -f name=${containerName}", returnStdout: true).trim()
                    
                    if (existingContainerId) {
                        echo "Stopping and removing existing Docker container '${containerName}' with ID: ${existingContainerId}..."
                        sh "docker stop ${existingContainerId}"
                        sh "docker rm ${existingContainerId}"
                    }
                    
                    // Docker 컨테이너 실행
                    echo "Starting Docker container '${containerName}' from image '${imageFullName}'..."
                    sh "docker run -d -e TZ=Asia/Seoul -p 8080:8080 --name ${containerName} ${imageFullName}"

                    // 컨테이너 상태 확인
                    def containerId = sh(script: "docker ps -q -f name=${containerName}", returnStdout: true).trim()
                    if (containerId) {
                        echo "Docker container '${containerName}' is running with ID: ${containerId}"
                    } else {
                        error "Failed to start Docker container '${containerName}'."
                    }
                }
            }
        }
    }
}