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


# Download Anaconda and install it
RUN wget --no-check-certificate https://repo.anaconda.com/archive/Anaconda3-2024.10-1-Linux-x86_64.sh -O /tmp/anaconda.sh && \
    bash /tmp/anaconda.sh -b -p /opt/conda && \
    rm /tmp/anaconda.sh

# Add Anaconda to the PATH
ENV PATH="/opt/conda/bin:$PATH"


# Install g++14
RUN add-apt-repository -y ppa:ubuntu-toolchain-r/ppa
RUN apt update && apt install -y gcc-14 g++-14 && apt clean
RUN update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-14 100 && \
    update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-14 100 && \
    update-alternatives --set gcc /usr/bin/gcc-14 && \
    update-alternatives --set g++ /usr/bin/g++-14

# Install SDKMAN!
RUN curl -s "https://get.sdkman.io" | bash && \
    bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
            sdk install java 23.0.1-oracle --default && \
            sdk install sbt && \
            sdk install scala 2.13.15 --default"

# Install verilator
RUN apt install -y verilator


# Download and install firtools
RUN FIRTOOL_URL=$(curl -s https://api.github.com/repos/llvm/circt/releases/latest | \
    jq -r '.assets[] | select(.name == "firrtl-bin-linux-x64.tar.gz") | .browser_download_url') && \
    wget --no-check-certificate $FIRTOOL_URL -O /tmp/firtool.tar.gz && \
    mkdir -p /tmp/firtool && \
    tar -xzf /tmp/firtool.tar.gz -C /tmp/firtool && \
    find /tmp/firtool -type f -name firtool -exec mv {} /usr/local/bin/ \; && \
    chmod +x /usr/local/bin/firtool && \
    rm -rf /tmp/firtool /tmp/firtool.tar.gz


# Download and install OSS-CAD Suite (contains yosys)
RUN OSS_CAD_URL=$(curl -s https://api.github.com/repos/YosysHQ/oss-cad-suite-build/releases/latest | \
    jq -r '.assets[] | select(.name | contains("linux-x64")) | .browser_download_url') && \
    wget --no-check-certificate $OSS_CAD_URL -O /tmp/oss-cad-suite.tar.xz && \
    mkdir -p /opt/oss-cad-suite && \
    tar -xf /tmp/oss-cad-suite.tar.xz -C /opt/oss-cad-suite --strip-components=1 && \
    rm /tmp/oss-cad-suite.tar.xz

# Add OSS-CAD Suite to PATH
ENV PATH="/opt/oss-cad-suite/bin:${PATH}"

RUN yosys --version
    

# Install netlistsvg (requires nodejs)
RUN curl -fsSL https://deb.nodesource.com/setup_22.x | bash - && \
    apt install -y nodejs && \
    npm install -g npm@latest

RUN npm install -g netlistsvg


# Download and install sv2v binary
RUN SV2V_URL=$(curl -s https://api.github.com/repos/zachjs/sv2v/releases/latest | \
    jq -r '.assets[] | select(.name == "sv2v-Linux.zip") | .browser_download_url') && \
    wget $SV2V_URL -O /tmp/sv2v.zip && \
    mkdir -p /tmp/sv2v && \
    unzip /tmp/sv2v.zip -d /tmp/sv2v && \
    find /tmp/sv2v -type f -name sv2v -exec mv {} /usr/local/bin/ \; && \
    chmod +x /usr/local/bin/sv2v && \
    rm -rf /tmp/sv2v /tmp/sv2v.zip

RUN sv2v --version

# Install rsvg-convert, and Inkscape
RUN apt update && apt install -y \
    librsvg2-bin \
    inkscape \
    && apt clean

RUN rsvg-convert --version
RUN inkscape --version

# Unset non-interactive mode after installation
ENV DEBIAN_FRONTEND=

# Set default shell to bash
SHELL ["/bin/bash", "-c"]

CMD ["bash"]
