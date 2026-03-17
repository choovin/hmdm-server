// TDD Test: Verify missing translations for contacts and ldap pages
// This test checks if the required translation keys exist in zh_CN.js

const fs = require('fs');
const path = require('path');

const zhCNPath = path.join(__dirname, 'server/src/main/webapp/localization/zh_CN.js');
const content = fs.readFileSync(zhCNPath, 'utf8');

// Required translation keys for contacts page
const contactsKeys = [
    'error.loading.contacts',
    'confirm.delete.contact',
    'confirm.delete.contact.message',
    'error.deleting.contact',
    'warning.no.contacts.selected',
    'warning.no.devices.selected',
    'error.syncing.contacts',
    'warning.contact.name.required',
    'error.saving.contact'
];

// Required translation keys for ldap page
const ldapKeys = [
    'warning.ldap.url.required',
    'warning.ldap.baseDn.required',
    'success.ldap.saved',
    'ldap.test.success',
    'ldap.test.failed'
];

function checkKeys(keys) {
    const missing = [];
    for (const key of keys) {
        // Check for the key in the format 'key': 'value' or "key": "value"
        const pattern = new RegExp(`['"]${key}['"]\\s*:`);
        if (!pattern.test(content)) {
            missing.push(key);
        }
    }
    return missing;
}

console.log('=== Testing Contacts Page Translations ===');
const missingContacts = checkKeys(contactsKeys);
if (missingContacts.length > 0) {
    console.log('FAIL: Missing translations for contacts page:');
    missingContacts.forEach(k => console.log('  - ' + k));
} else {
    console.log('PASS: All contacts translations exist');
}

console.log('\n=== Testing LDAP Page Translations ===');
const missingLdap = checkKeys(ldapKeys);
if (missingLdap.length > 0) {
    console.log('FAIL: Missing translations for ldap page:');
    missingLdap.forEach(k => console.log('  - ' + k));
} else {
    console.log('PASS: All LDAP translations exist');
}

if (missingContacts.length > 0 || missingLdap.length > 0) {
    console.log('\n=== TDD Result: RED (Tests Failed) ===');
    process.exit(1);
} else {
    console.log('\n=== TDD Result: GREEN (All Tests Passed) ===');
    process.exit(0);
}