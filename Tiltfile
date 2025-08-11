docker_build('product-service', '.')
k8s_yaml('k8s/deployment.yaml')
k8s_resource('product-service', port_forwards=8000)

local_resource(
  'example-java-compile',
  'mvn clean package -DskipTests',
  deps=['src', 'pom.xml'],
  resource_deps=['deploy'])

docker_build(
  'example-java-image',
  './target',
  dockerfile='./Dockerfile')