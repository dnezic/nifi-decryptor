/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hr.trialogic.nifi.properties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.nifi.encrypt.PropertyEncryptor;
import org.apache.nifi.encrypt.PropertyEncryptorBuilder;
import org.apache.nifi.flow.encryptor.StandardFlowEncryptor;
import org.apache.nifi.security.util.EncryptionMethod;

public class Decryptor {

    private static final Pattern ENCRYPTED_PATTERN = Pattern.compile(".*enc\\{([^\\}]+?)\\}.*");

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Params: <sensitive_value (enc{xyz})> <key> <algo_old>");
            System.out.println("For empty secret enter: nififtw!");
            System.out.println("Supported algos: \n");
            for (EncryptionMethod a : EncryptionMethod.values()) {
                System.out.println(a.getAlgorithm());
            }
            System.exit(-1);
        }

        String sensitive = args[0];
        String keyIn = args[1];
        String algoIn = args[2];
        // String keyOut = args[3];
        // String algoOut = args[4];

        System.out.println(
                "Params: key: " + keyIn + " algo in: " + algoIn + " sensitive: " + sensitive);

        Decryptor j = new Decryptor();
        PropertyEncryptor inputEncryptor;
        // PropertyEncryptor outputEncryptor;
        CustomizedFlowEncryptor flowEncryptor;

        flowEncryptor = j.new CustomizedFlowEncryptor();

        final Matcher matcher = ENCRYPTED_PATTERN.matcher(sensitive);

        boolean matched = false;
        while (matcher.find()) {
            matched = true;
            sensitive = matcher.group(1);
            inputEncryptor = j.getPropertyEncryptor(keyIn, algoIn);
            // outputEncryptor = j.getPropertyEncryptor(keyOut,
            // EncryptionMethod.forAlgorithm(algoOut).name());
            String decr = flowEncryptor.getOutputDecrypted(sensitive, inputEncryptor, null);
            System.out.println(decr);
        }
        if(!matched) {
            System.out.println("Not matched enc{} sensitive value");
        }

    }

    private PropertyEncryptor getPropertyEncryptor(final String propertiesKey, final String propertiesAlgorithm) {
        return new PropertyEncryptorBuilder(propertiesKey).setAlgorithm(propertiesAlgorithm).build();
    }

    class CustomizedFlowEncryptor extends StandardFlowEncryptor {
        public String getOutputDecrypted(final String inputEncrypted, final PropertyEncryptor inputEncryptor,
                final PropertyEncryptor outputEncryptor) {

            String inputDecrypted = inputEncryptor.decrypt(inputEncrypted);
            return inputDecrypted;

            // final String outputEncrypted = outputEncryptor.encrypt(inputDecrypted);
            // return outputEncrypted;
        }

    }
}
