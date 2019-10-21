from setuptools import setup,find_packages

setup(name='trainer',
    version='1.0',
    packages=find_packages(),
    description='my project',
    author='saumil',
    author_email='saumil.dedhia@somaiya.edu',
    install_requires=[
       'keras',
       'h5py',
  ],
  zip_safe=False
)
