def ttcall() {
echo "xxx world hello"
}


def buildImage(imageName,args,dockerFilePath) {
                    sh(script: """
                  cd $WORKSPACE/${dockerFilePath}
                  docker build --build-arg REGISTRY=containerregistrydev.azurecr.io -t "${CONTAINERREGISTRY}/webjet/alertbot":$BUILD_NUMBER .
                        """, returnStdout: true)


}

