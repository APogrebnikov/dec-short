var LssClient = function ($) {
    var defaultExtension = '.doc';
    var usingByPass = false;
    var defaultTimeout = 0;
    var checkLssConnectivityTimeout = 10000;
    
    YokuServiceClient = function () {
        $.support.cors = true;
        var httpBaseUrl = "http://127.0.0.1:61111/webhost";
        var sslBaseUrl = "http://127.0.0.1:61111/webhost";
        //var sslBaseUrl = "https://127.0.0.1:61112/webhost";
        var POST = 'POST';
        
        function post(type, data, timeout) {
            return $.ajax({
                url: createUrl(POST, type),
                type: POST,
                data: JSON.stringify(data),
                contentType: "application/json",
                crossDomain: true,
                processData: false,
                dataType: "json",
                timeout: timeout
            });
        }

        function createUrl(methodName, type) {
            return getBaseUrl() + '/' + methodName + '?' + 'type' + '=' + type;
        }

        function getBaseUrl() {
            return window.location.protocol == "https:" ? sslBaseUrl : httpBaseUrl;
        }

        return {
            post: post
        };
    }

    function HeartBeatRequest() {
        var self = this;
        self.taskType = 'SystemStateTask';
        self.Payload = 'CheckLssPresence';
    }

    function SignRequest(data, certificate) {
        var self = this;
        self.taskType = 'SignTask';
        self.DataToSign = data;
        self.SignCertificate = certificate;
    }

    function SignVerificationRequest(signedData, originalData) {
        var self = this;
        self.taskType = 'VerifyTask';
        self.SignedData = signedData;
        self.OriginalData = originalData;
    }
    
    function EncryptionRequest(data, certificates) {
        var self = this;
        self.taskType = 'EncryptTask';
        self.DataToEncrypt = data;
        self.Certificates = certificates;
    }
    
    function DecryptionRequest(data) {
        var self = this;
        self.taskType = 'DecryptTask';
        self.EncryptedData = data;
    }

    function SignAndEncryptionRequest(data, certificates, signCertificate) {
        var self = this;
        self.taskType = 'SignAndEncryptTask';
        self.DataToSignAndEncrypt = data;
        self.Certificates = certificates;
        self.SignCertificate = signCertificate;
    }
    
    function DecryptAndVerifySignRequest(data, certificate) {
        var self = this;
        self.taskType = 'DecryptAndVerifyTask';
        self.EnctyptedDataAndSign = data;
    }
    
    function SelectCertificateRequest() {
        var self = this;
        self.taskType = 'SelectCertificateTask';
    }

    function HashRequest() {
        var self = this;
        self.taskType = 'HashTask';
    }

    function SignedContentRequest() {
        var self = this;
        self.taskType = 'SignedContentTask';
    }

    function SpecialSign01Request() {
        var self = this;
        self.taskType = 'SpecialSign01Task';
    }
    
    function CertificateRequestMessage(subject, extendedKeyUsages) {
        var self = this;
        self.taskType = 'CreateCertificateRequestTask';
        self.Subject = subject;
        self.ExtendedKeyUsages = extendedKeyUsages;
    }

    function InstallCertificateIssuedByRequestMessage(certificate) {
        var self = this;
        self.taskType = 'InstallCertificateIssuedByRequestTask';
        self.Certificate = certificate;
    }
    
    function generateGuid() {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c == 'x' ? r : (r & 0x7 | 0x8)).toString(16);
        });
        return uuid;
    }


    function sendRequest(task, timeout) {
        var client = new YokuServiceClient();
        if (usingByPass) {
            task.ByPassVisualization = true;
        }
        return client.post(task.taskType, task, timeout);
    }

    function installCertificateIssuedByRequest(options) {
        var defaultOptions = {
            requestId: generateGuid()
        };

        options = $.extend(defaultOptions, options);

        var task = new InstallCertificateIssuedByRequestMessage(options.certificate);
        task.RequestId = options.requestId;
        return sendRequest(task, defaultTimeout);
    }
    
    function generateCertificateRequest(options) {
        var defaultOptions = {
            requestId: generateGuid()
        };

        options = $.extend(defaultOptions, options);

        var task = new CertificateRequestMessage(
            options.subject,
            options.extendedKeyUsages);
        task.RequestId = options.requestId;
        return sendRequest(task, defaultTimeout);
    }

    function sign (options) {
        
        var defaultOptions = {
            base64Data : '-',
            description: 'Описание не задано',
            documentName: 'Подпись',
            fileExtension : defaultExtension,
            isAttached: true,
            base64Certificate: '-',
            disableCertificateVerification: false,
            requestId: generateGuid()
        };

        options = $.extend(defaultOptions, options);

        var task = new SignRequest(options.base64Data, options.base64Certificate);
        task.IsAttached = options.isAttached;
        task.DocumentName = options.documentName;
        task.Description = options.description;
        task.ViewDescriptor = { FileExtension: options.fileExtension };
        task.RequestId = options.requestId;
        task.TspServerUrl = options.tspServerUrl;
        task.TspServerTimeoutInMilliseconds = parseInt(options.tspServerTimeout);
        task.DisableCertificateVerification = options.disableCertificateVerification;

        return sendRequest(task, defaultTimeout);
    }

    function verifySign(options) {
        var defaultOptions = {
            base64Data: '-',
            base64DataWithoutSign: '-',
            isAttached: true,
            description: 'Описание не задано',
            documentName: 'Проверка подписи',
            fileExtension: defaultExtension,
            requestId: generateGuid()
        };

        options = $.extend(defaultOptions, options);

        var task = new SignVerificationRequest(options.base64Data, options.base64DataWithoutSign);
        task.IsAttached = options.isAttached;
        task.DocumentName = options.documentName;
        task.Description = options.description;
        task.ViewDescriptor = { FileExtension: options.fileExtension };
        task.RequestId = options.requestId;

        return sendRequest(task, defaultTimeout);
    }

    function encrypt (options) {
        var defaultOptions = {
            base64Data: '-',
            base64Certificates: [],
            description: 'Описание не задано',
            documentName: 'Шифрование',
            fileExtension: defaultExtension,
            disableCertificateVerification: false,
            requestId: generateGuid()
        };

        options = $.extend(defaultOptions, options);
        
        var task = new EncryptionRequest(options.base64Data, options.base64Certificates);
        task.DocumentName = options.documentName;
        task.Description = options.description;
        task.ViewDescriptor = { FileExtension: options.fileExtension };
        task.DisableCertificateVerification = options.disableCertificateVerification;
        task.RequestId = options.requestId;

        return sendRequest(task, defaultTimeout);
    }

    function decrypt(options) {
        var defaultOptions = {
            base64Data: '-',
            description: 'Описание не задано',
            documentName: 'Расшифрование',
            fileExtension: defaultExtension,
            disableCertificateVerification: false,
            requestId: generateGuid()
        };
        
        options = $.extend(defaultOptions, options);
        var task = new DecryptionRequest(options.base64Data);
        task.DocumentName = options.documentName;
        task.Description = options.description;
        task.ViewDescriptor = { FileExtension: options.fileExtension };
        task.DisableCertificateVerification = options.disableCertificateVerification;
        task.RequestId = options.requestId;

        return sendRequest(task, defaultTimeout);
    }

    function signAndEncrypt(options) {
        var defaultOptions = {
            base64Data: '-',
            base64Certificates: [],
            description: 'Описание не задано',
            documentName: 'Подпись и шифрование',
            fileExtension: defaultExtension,
            base64SignCertificate: '-',
            disableCertificateVerification: false,
            requestId: generateGuid()
        };
        
        options = $.extend(defaultOptions, options);
        var task = new SignAndEncryptionRequest(options.base64Data, options.base64Certificates, options.base64SignCertificate);
        task.DocumentName = options.documentName;
        task.Description = options.description;
        task.ViewDescriptor = { FileExtension: options.fileExtension };
        task.TspServerUrl = options.tspServerUrl;
        task.TspServerTimeoutInMilliseconds = parseInt(options.tspServerTimeout);
        task.DisableCertificateVerification = options.disableCertificateVerification;
        task.RequestId = options.requestId;

        return sendRequest(task, defaultTimeout);
    }

    function decryptAndVerifySign(options) {
        var defaultOptions = {
            base64Data: '-',
            description: 'Описание не задано',
            documentName: 'Расшифрование и проверка подписи',
            fileExtension: defaultExtension,
            disableCertificateVerification: false,
            requestId: generateGuid()
        };

        options = options = $.extend(defaultOptions, options);
        var task = new DecryptAndVerifySignRequest(options.base64Data);
        task.DocumentName = options.documentName;
        task.Description = options.description;
        task.ViewDescriptor = { FileExtension: options.fileExtension };
        task.DisableCertificateVerification = options.disableCertificateVerification;
        task.RequestId = options.requestId;

        return sendRequest(task, defaultTimeout);
    }

    function selectCertificate(options) {
        var defaultOptions = {
            disableCertificateVerification: false,
            requestId: generateGuid()
        };
        options = $.extend(defaultOptions, options);
        var task = new SelectCertificateRequest();
        task.DisableCertificateVerification = options.disableCertificateVerification;
        task.RequestId = options.requestId;
        
        return sendRequest(task, defaultTimeout);
    }

    function hash(options) {
        var defaultOptions = {
            requestId: generateGuid()
        };
        options = $.extend(defaultOptions, options);
        var task = new HashRequest();
        task.RequestId = options.requestId;
        task.DataToHash = options.base64Data;

        return sendRequest(task, defaultTimeout);
    }

    function getSignedContent(options) {
        var defaultOptions = {
            requestId: generateGuid()
        };

        options = $.extend(defaultOptions, options);

        var task = new SignedContentRequest();
        task.RequestId = options.requestId;
        task.SignedData = options.base64Data;

        return sendRequest(task, defaultTimeout);
    }

    function specialSign01(options) {
        var defaultOptions = {
            requestId: generateGuid()
        };

        options = $.extend(defaultOptions, options);

        var task = new SpecialSign01Request();
        task.RequestId = options.requestId;
        task.DataToSign = options.base64Data;
        task.SignCertificate = options.base64Certificate;

        return sendRequest(task, defaultTimeout);
    }

    function checkLssConnectivity() {
        var task = new HeartBeatRequest();
        return sendRequest(task, checkLssConnectivityTimeout);
    }

    function withBypass() {
        usingByPass = true;
        return this;
    }

    return {
        sign: sign,
        verifySign: verifySign,
        encrypt: encrypt,
        decrypt: decrypt,
        signAndEncrypt: signAndEncrypt,
        decryptAndVerifySign: decryptAndVerifySign,
        selectCertificate: selectCertificate,
        checkConnection: checkLssConnectivity,
        withBypass: withBypass,
        generateCertificateRequest: generateCertificateRequest,
        installCertificateIssuedByRequest: installCertificateIssuedByRequest,
        hash: hash,
        getSignedContent: getSignedContent,
        specialSign01: specialSign01
    }
}