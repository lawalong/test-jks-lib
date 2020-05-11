def deploy(region,environment,nameSpace,appName) {
    echo "333 Deploying ${appName}-wjau to ${nameSpace} ..."
    url = "http://kubebot.default/deploy/dev/${nameSpace}/${appName}/${BUILD_NUMBER}?registry=$CONTAINERREGISTRY&repository=webjet"
    db = "@$WORKSPACE/pipeline/deploy.yaml"

echo db;
    sh '''


    rep=$(curl -s -X POST '''+url+''' \
    --data-binary "@/home/jenkins/workspace/platform-team/test-deploy/pipeline/deploy.yaml" \
                            -H 'Content-Type: application/yaml' \
                            -H 'Expect:' \
                            -D -
    )

    echo "$rep"

    '''


}
