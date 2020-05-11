def deploy(region,environment,nameSpace,appName) {
    echo "333 Deploying ${appName}-wjau to ${nameSpace} ..."
    url2 = "http://kubebot.default/deploy/dev/${nameSpace}/${appName}/${BUILD_NUMBER}?registry=$CONTAINERREGISTRY&repository=webjet"
    sh '''


    rep=$(curl -s -X POST '''+url2+''' )



    '''


}
