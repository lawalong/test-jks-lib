createImage(appName,appProject,appVersion,isProd) {
  withCredentials(bindings: [usernamePassword(credentialsId: 'acrcredentials', 
                                              passwordVariable: 'DOCKER_REGISTRY_PASSWORD', 
                                              usernameVariable: 'DOCKER_REGISTRY_USER')]) {
    sh "docker login -u ${DOCKER_REGISTRY_USER} -p ${DOCKER_REGISTRY_PASSWORD} ${DOCKER_REGISTRY}"
  }
  if(!isProd){
    echo 'Building Docker image...'
    sh "docker build . --file Dockerfile --tag ${DOCKER_REGISTRY}/${appProject}/${appName}:${appVersion}-${BUILD_NUMBER}-dev"
    echo 'Pushing image to Registry...'
    sh "docker push ${DOCKER_REGISTRY}/${appProject}/${appName}:${appVersion}-${BUILD_NUMBER}-dev"
  } else {
    echo 'Building Docker image...'
    sh "docker tag ${DOCKER_REGISTRY}/${appProject}/${appName}:${appVersion}-${BUILD_NUMBER}-dev \
 		   ${DOCKER_REGISTRY}/${appProject}/${appName}:${appVersion}-${BUILD_NUMBER}"
    echo 'Pushing image to Registry...'
    sh "docker push ${DOCKER_REGISTRY}/${appProject}/${appName}:${appVersion}-${BUILD_NUMBER}"
  }
}


