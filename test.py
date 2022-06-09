

import os, sys
import subprocess

with open(sys.argv[1]) as f:
    for line in f:
        subprocess.call(["java", "-jar", "target/nifi-decryptor-0.1-jar-with-dependencies.jar", line.strip(), "nififtw!", "PBEWITHMD5AND256BITAES-CBC-OPENSSL"])
