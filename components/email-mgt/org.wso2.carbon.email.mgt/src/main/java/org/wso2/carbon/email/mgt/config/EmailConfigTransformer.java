/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.email.mgt.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.email.mgt.dto.EmailTemplateDTO;
import org.wso2.carbon.email.mgt.exceptions.I18nMgtEmailConfigException;
import org.wso2.carbon.identity.base.IdentityException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/**
 * This class is used to transform <code>Properties</code> to <code>EmailTemplateDTO</code>
 * and vice versa.
 */
public class EmailConfigTransformer {

    private EmailConfigTransformer() {
    }

    private static final Log log = LogFactory.getLog(EmailConfigTransformer.class);

    public static EmailTemplateDTO[] transform(Properties props) throws I18nMgtEmailConfigException {

        List<EmailTemplateDTO> emailTemplates = new ArrayList<EmailTemplateDTO>();

        if (log.isDebugEnabled()) {
            log.debug("Transforming Properties to EmailTemplateDTO[]");
        }

        Set<String> keySet = props.stringPropertyNames();
        String emailTemplateLanguage;
        String emailTemplateDisplayType;
        String emailContentType;

        for (String key : keySet) {
            EmailTemplateDTO template = new EmailTemplateDTO();
            String[] emailTemplateKeyArray = key.split("\\|");
            template.setName(emailTemplateKeyArray[0]);
            emailTemplateDisplayType = emailTemplateKeyArray[1];
            emailContentType = emailTemplateKeyArray[2];

            // Retrieves the content of each email template.
            String[] emailTemplateContent = props.getProperty(key).split("\\|");
            if (emailTemplateContent.length > 3) {
                throw new I18nMgtEmailConfigException("Cannot have | character in the template");
            }

            String[] emailTypeKeyArray = emailTemplateKeyArray[0].split("\\.");
            String [] emailLocalCodeArray = emailTypeKeyArray[1].split("_");
            Locale localeObject = new Locale(emailLocalCodeArray[0],emailLocalCodeArray[1]);

            emailTemplateLanguage = localeObject.getDisplayName();
            String subject = emailTemplateContent[0];
            String body = emailTemplateContent[1];
            String footer = emailTemplateContent[2];

            if (log.isDebugEnabled()) {
                log.debug("Template info - name: " + key + " locale: " +
                        emailTemplateLanguage + " subject: " + subject +
                        " body: " + body + " footer: " + footer + " displayName:" + emailTemplateDisplayType);
            }

            template.setLocale(emailTemplateLanguage);
            template.setSubject(subject);
            template.setBody(body);
            template.setFooter(footer);
            template.setDisplayName(emailTemplateDisplayType);
            template.setEmailContentType(emailContentType);

            emailTemplates.add(template);
        }

        return emailTemplates.toArray(new EmailTemplateDTO[emailTemplates.size()]);
    }

    public static Properties transform(EmailTemplateDTO template) throws IdentityException {
        Properties props = new Properties();
        StringBuilder contents = new StringBuilder();
        contents.append(template.getSubject()).append("|").append(template.getBody()).append("|").
                append(template.getFooter());
        props.setProperty(template.getName(), contents.toString());
        return props;
    }
}
