# Quarkus-ref-implementation - Canary rollout with ISTIO

## POC:
```
a) Canary Deployment with A/B Testing
b) Service Discovery
c) Istio Ingress Gateway
d) Horizontal Pod Autoscaller
```
## Prerequisite

```
a) Kubernetes Cluster (tested on v1.18.12-gke.1210)
b) Istio Instalation (tested on istio-1.9.0)
C) Kiali

```
##  Reference Architecture:
![Capture](https://user-images.githubusercontent.com/16347988/110306608-eb84dc00-7ff5-11eb-9d8a-aed13b6829d0.JPG)
![Capture_1](https://user-images.githubusercontent.com/16347988/110306631-f0499000-7ff5-11eb-84ba-21090dda7ac3.JPG)

##  Steps:

```
git clone https://github.com/mosesalphonse/quarkus-ref-implementation.git

cd quarkus-ref-implementation

```
##  Build Workloads and Push images into Registry:

### country-ext-rest-client (ExtService):
```
	cd workloads/country-ext-rest-client

	mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.push=true
  
```
### country-sql-client(IntService):
```
	cd workloads/country-sql-client

	mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.push=true
  
```
### frontend:
```
	cd workloads/frontend
	
	mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.push=true
```
## Deployments:

## Setup Metric Server(to enable HPA):

```
	kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
	
```

### Initial Workloads for A/B testing:
```
	kubectl create -f  workloads/frontend/yaml/service-account.yaml
	kubectl create -f  workloads/frontend/yaml/deployment-v1.yaml
	kubectl create -f  workloads/frontend/yaml/deployment-v2.yaml
	kubectl create -f  workloads/frontend/yaml/service.yaml
	kubectl autoscale deployment frontend-v1 --cpu-percent=40 --min=1 --max=3
	kubectl autoscale deployment frontend-v2 --cpu-percent=40 --min=1 --max=3
```
## Network Config for A/B Testing (50-50 to V1 and V2 of the frontend):

```
	kubectl create -f  networks/AB-VirtualService.yaml
	kubectl create -f  networks/AB-DestinationRule.yaml
	kubectl create -f  networks/AB-Gateway.yaml
	
```

### Deploy new verision(V3) and its Dependency(ExtService & IntService) - Canary Release:
```
	kubectl create -f  workloads/frontend/yaml/deployment-v3.yaml
	kubectl create -f workloads/postgres/yamls/postgres.yaml
	kubectl create -f  workloads/country-ext-rest-client/yaml/manifest.yaml
	kubectl create -f  workloads/country-sql-client/yaml/manifest.yaml
	kubectl autoscale deployment frontend-v3 --cpu-percent=40 --min=1 --max=3
	kubectl autoscale deployment extservice-v1 --cpu-percent=40 --min=1 --max=3
	kubectl autoscale deployment intservice-v1 --cpu-percent=40 --min=1 --max=3
```

### Update Network Config - Canary Release:
```
	kubectl delete -f  networks/AB-VirtualService.yaml
	kubectl delete -f  networks/AB-DestinationRule.yaml
	kubectl apply -f  networks/Canary-VirtualService.yaml
	kubectl apply -f  networks/Canary-DestinationRule.yaml

```

### Promote tested version - Update Network Config:
```
	kubectl delete -f  networks/Canary-VirtualService.yaml
	kubectl delete -f  networks/Canary-DestinationRule.yaml
	kubectl apply -f  networks/Canary-Promote-VirtualService.yaml
	kubectl apply -f  networks/Canary-Promote-DestinationRule.yaml

```

### Remove Older versions:
```
	kubectl delete -f  workloads/frontend/yaml/deployment-v1.yaml
	kubectl delete -f  workloads/frontend/yaml/deployment-v2.yaml
	kubectl delete hpa frontend-v1
	kubectl delete hpa frontend-v2
```

### Uninstall:
```
	kubectl delete -f  workloads/frontend/yaml/service-account.yaml
	kubectl delete -f  workloads/frontend/yaml/deployment-v3.yaml
	kubectl delete -f  workloads/frontend/yaml/service.yaml
	kubectl delete -f  networks/AB-VirtualService.yaml
	kubectl delete -f  networks/AB-DestinationRule.yaml
	kubectl delete -f  networks/AB-Gateway.yaml
	kubectl delete -f workloads/postgres/yamls/postgres.yaml
	kubectl delete -f workloads/country-ext-rest-client/yaml/manifest.yaml
	kubectl delete -f workloads/country-sql-client/yaml/manifest.yaml
	kubectl delete hpa extservice-v1
	kubectl delete hpa frontend-v3
	kubectl delete hpa intservice-v1
```

###  Enable Ingress through ISTIO Ingress gateway to Frontend workload:
	- Use Kiali Console to create Request Routing & Gateway
	- Enable canary deployment using Kiali dashboard

Invoke Frontend App using istio-ingressgateway's public IP
```
curl http://{istio-ingress-externalip}/home?user=india  # traffics will be distributed into v1, v2 and V3 of the frontend workloads equally

curl -H 'username: moses' http://ingress-uri/home?user=uk  # traffic will always be routed to v3 because we pass username=moses in the header. Using A/B testing business users can able to test the code in LIVE before rolling it out to all the users.

```

## General Testing endpoints:
```
http://{istio-ingress-externalip}/

http://{istio-ingress-externalip}/home?user=india

```
### generare some traffic:
```
for ((i=1;i<=2000;i++)); do   curl -v --header "Connection: keep-alive" "http://ingressip/home?user=uk"; done
```

### List the namespaces labeld for istio Sidecar injection:
```
	kubectl get namespace -L istio-injection
	
```
### Use Token Auth strategy for Kiali:
```
	Use 'token' for authentication.
	Note: you may have to update the kilai.yaml as 'strategy: token' instead of 'strategy: anonymous'
	
To Obtain Token:
	
	kubectl -n istio-system get sa
	
	kubectl -n istio-system describe sa <kiali servive account name>
	
	kubectl -n istio-system  describe secret <Mountable secrets>
	
```
