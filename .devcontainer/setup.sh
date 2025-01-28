#!/bin/bash

echo "Creating octonyte conda environment. This will take some time (minutes)"

# Ensure Conda is initialized for the current shell
eval "$(conda shell.bash hook)"

# Create the Conda environment
conda env create -f conda_env.yml -n octonyte 

# Clean up unnecessary files
conda clean -a --yes

# Initialize bashrc
conda init

# display environments
conda info --envs

# Add 'conda activate adaptive' to ~/.bashrc if not already present
if ! grep -q "conda activate octonyte" ~/.bashrc; then
    echo "Adding 'conda activate octonyte' to ~/.bashrc"
    echo -e "\n# Activate the octonyte conda environment by default\nconda activate octonyte" >> ~/.bashrc
fi

# Activate the environment
conda activate octonyte


echo "Setup completed successfully!"
