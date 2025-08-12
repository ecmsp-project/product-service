
# include('../order-service/Tiltfile')

local_resource(
    'product-service-compile',
    'mvn package -DskipTests -DfailOnNoContracts=false && ' +
    'unzip -o target/product-service-0.0.1-SNAPSHOT.jar -d target/jar',
    deps=['src', 'pom.xml'],
)

docker_build(
    'product-service',
    './target/jar',
    dockerfile='./DockerfileTilt')

k8s_yaml('k8s/deployment.yaml')
k8s_yaml('k8s/service.yaml')

k8s_resource(
    'product-service',
    port_forwards=8400,
    resource_deps=['product-service-compile']
)