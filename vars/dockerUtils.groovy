def ttcall() {
echo "xxx world hello"
}


def buildImage(imageName,args,dockerFilePath) {
                  sh(script: """
                  cd $WORKSPACE/${dockerFilePath}
                  docker build ${args} -t "${CONTAINERREGISTRY}/webjet/${imageName}}":$BUILD_NUMBER .
                        """, returnStdout: true)


}

