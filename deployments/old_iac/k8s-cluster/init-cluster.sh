#!/bin/bash

multipass launch --name master-k8s \
          --cpus 2 \
          --mem 2048M \
          --disk 10G \
          --cloud-init node-k8s.yaml
multipass launch --name worker1-k8s \
          --cpus 2 \
          --mem 2048M \
          --disk 10G \
          --cloud-init node-k8s.yaml
multipass launch --name worker2-k8s \
          --cpus 2 \
          --mem 2048M \
          --disk 10G \
          --cloud-init node-k8s.yaml

