FROM ubuntu:24.04

RUN apt-get update -y


# Set non-interactive mode for APT to avoid prompts
ENV DEBIAN_FRONTEND=noninteractive

# Update and install basic tools
RUN apt update && apt install -y \
    software-properties-common \
    curl \
    zip \
    unzip \
    tar \
    ca-certificates \
    git \
    wget \
    build-essential \
    vim \
    jq \
    && apt clean

# Unset non-interactive mode after installation
ENV DEBIAN_FRONTEND=

# Set default shell to bash
SHELL ["/bin/bash", "-c"]

CMD ["bash"]
