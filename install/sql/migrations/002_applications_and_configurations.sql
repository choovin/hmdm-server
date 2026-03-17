-- Applications and Configurations Schema
-- Creates tables: icons, applications, applicationversions, configurations, configurationapplications

-- Icons table
CREATE TABLE IF NOT EXISTS icons (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    filePath VARCHAR(500),
    customerId INTEGER REFERENCES customers(id) ON DELETE CASCADE
);

-- Applications table
CREATE TABLE IF NOT EXISTS applications (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    pkg VARCHAR(255) NOT NULL,
    version VARCHAR(50),
    versionCode INTEGER DEFAULT 0,
    arch VARCHAR(50),
    url TEXT,
    split BOOLEAN DEFAULT FALSE,
    urlArmeabi TEXT,
    urlArm64 TEXT,
    showIcon BOOLEAN DEFAULT TRUE,
    useKiosk BOOLEAN DEFAULT FALSE,
    system BOOLEAN DEFAULT FALSE,
    latestVersion BOOLEAN DEFAULT TRUE,
    runAfterInstall BOOLEAN DEFAULT FALSE,
    runAtBoot BOOLEAN DEFAULT FALSE,
    skipVersion INTEGER,
    iconText VARCHAR(200),
    type VARCHAR(50) DEFAULT 'app',
    iconId INTEGER REFERENCES icons(id),
    screenOrder INTEGER DEFAULT 0,
    keyCode INTEGER,
    bottom BOOLEAN DEFAULT FALSE,
    longTap BOOLEAN DEFAULT FALSE,
    intent TEXT,
    remove BOOLEAN DEFAULT FALSE,
    action TEXT,
    customerId INTEGER REFERENCES customers(id) ON DELETE CASCADE,
    commonApplication BOOLEAN DEFAULT FALSE,
    deletionProhibited BOOLEAN DEFAULT FALSE,
    outdated BOOLEAN DEFAULT FALSE,
    latestVersionText VARCHAR(50)
);

-- Application versions table
CREATE TABLE IF NOT EXISTS applicationversions (
    id SERIAL PRIMARY KEY,
    applicationId INTEGER NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    version VARCHAR(50) NOT NULL,
    versionCode INTEGER DEFAULT 0,
    url TEXT,
    urlArmeabi TEXT,
    urlArm64 TEXT,
    split BOOLEAN DEFAULT FALSE
);

-- Configurations table
CREATE TABLE IF NOT EXISTS configurations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    type INTEGER DEFAULT 0,
    password VARCHAR(100),
    customerId INTEGER REFERENCES customers(id) ON DELETE CASCADE,
    blockStatusBar BOOLEAN DEFAULT FALSE,
    systemUpdateType INTEGER DEFAULT 0,
    systemUpdateFrom TIME,
    systemUpdateTo TIME,
    scheduleAppUpdate BOOLEAN DEFAULT FALSE,
    appUpdateFrom TIME,
    appUpdateTo TIME,
    downloadUpdates VARCHAR(50) DEFAULT 'wifi',
    gps BOOLEAN DEFAULT TRUE,
    bluetooth BOOLEAN DEFAULT TRUE,
    wifi BOOLEAN DEFAULT TRUE,
    mobileData BOOLEAN DEFAULT TRUE,
    usbStorage BOOLEAN DEFAULT TRUE,
    requestUpdates BOOLEAN DEFAULT TRUE,
    disableLocation BOOLEAN DEFAULT FALSE,
    appPermissions VARCHAR(50) DEFAULT 'auto',
    pushOptions VARCHAR(50) DEFAULT 'mqtt',
    keepaliveTime INTEGER DEFAULT 120,
    autoBrightness BOOLEAN DEFAULT TRUE,
    brightness INTEGER DEFAULT 50,
    manageTimeout BOOLEAN DEFAULT FALSE,
    timeout INTEGER DEFAULT 0,
    lockVolume BOOLEAN DEFAULT FALSE,
    manageVolume BOOLEAN DEFAULT FALSE,
    volume INTEGER DEFAULT 50,
    passwordMode VARCHAR(50) DEFAULT 'device',
    orientation INTEGER DEFAULT 0,
    runDefaultLauncher BOOLEAN DEFAULT FALSE,
    disableScreenshots BOOLEAN DEFAULT FALSE,
    autostartForeground BOOLEAN DEFAULT FALSE,
    timeZone VARCHAR(100),
    allowedClasses TEXT,
    newServerUrl TEXT,
    lockSafeSettings BOOLEAN DEFAULT FALSE,
    permissive BOOLEAN DEFAULT FALSE,
    kioskExit VARCHAR(100),
    showWifi BOOLEAN DEFAULT FALSE,
    mainAppId INTEGER,
    eventReceivingComponent VARCHAR(255),
    kioskMode BOOLEAN DEFAULT FALSE,
    contentAppId INTEGER,
    wifiSSID VARCHAR(100),
    wifiPassword VARCHAR(100),
    wifiSecurityType VARCHAR(50) DEFAULT 'none',
    encryptDevice BOOLEAN DEFAULT FALSE,
    qrParameters TEXT,
    adminExtras TEXT,
    mobileEnrollment BOOLEAN DEFAULT FALSE,
    kioskHome BOOLEAN DEFAULT FALSE,
    kioskRecents BOOLEAN DEFAULT FALSE,
    kioskNotifications BOOLEAN DEFAULT FALSE,
    kioskSystemInfo BOOLEAN DEFAULT FALSE,
    kioskKeyguard BOOLEAN DEFAULT FALSE,
    kioskLockButtons BOOLEAN DEFAULT FALSE,
    kioskScreenOn BOOLEAN DEFAULT TRUE,
    launcherUrl TEXT,
    restrictions TEXT,
    useDefaultDesignSettings BOOLEAN DEFAULT TRUE,
    backgroundColor VARCHAR(20) DEFAULT '#FFFFFF',
    textColor VARCHAR(20) DEFAULT '#000000',
    backgroundImageUrl TEXT,
    iconSize INTEGER DEFAULT 48,
    desktopHeader BOOLEAN DEFAULT TRUE,
    desktopHeaderTemplate VARCHAR(255),
    displayStatus BOOLEAN DEFAULT TRUE,
    qrCodeKey VARCHAR(255)
);

-- Configuration applications mapping
CREATE TABLE IF NOT EXISTS configurationapplications (
    id SERIAL PRIMARY KEY,
    configurationId INTEGER NOT NULL REFERENCES configurations(id) ON DELETE CASCADE,
    applicationId INTEGER NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    applicationVersionId INTEGER REFERENCES applicationversions(id),
    screenOrder INTEGER DEFAULT 0,
    remove BOOLEAN DEFAULT FALSE,
    showIcon BOOLEAN DEFAULT TRUE,
    iconText VARCHAR(200),
    keyCode INTEGER,
    bottom BOOLEAN DEFAULT FALSE,
    longTap BOOLEAN DEFAULT FALSE,
    intent TEXT,
    action TEXT,
    UNIQUE(configurationId, applicationId)
);

-- Configuration files mapping
CREATE TABLE IF NOT EXISTS configurationfiles (
    id SERIAL PRIMARY KEY,
    configurationId INTEGER NOT NULL REFERENCES configurations(id) ON DELETE CASCADE,
    fileId INTEGER NOT NULL,
    remove BOOLEAN DEFAULT FALSE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_applications_customer ON applications(customerId);
CREATE INDEX IF NOT EXISTS idx_applications_pkg ON applications(pkg);
CREATE INDEX IF NOT EXISTS idx_applications_common ON applications(commonApplication);
CREATE INDEX IF NOT EXISTS idx_appversions_application ON applicationversions(applicationId);
CREATE INDEX IF NOT EXISTS idx_configurations_customer ON configurations(customerId);
CREATE INDEX IF NOT EXISTS idx_configapps_configuration ON configurationapplications(configurationId);
CREATE INDEX IF NOT EXISTS idx_configapps_application ON configurationapplications(applicationId);
CREATE INDEX IF NOT EXISTS idx_icons_customer ON icons(customerId);
