#!/bin/bash

multipass delete master-k8s
multipass delete worker1-k8s
multipass delete worker2-k8s
multipass purge