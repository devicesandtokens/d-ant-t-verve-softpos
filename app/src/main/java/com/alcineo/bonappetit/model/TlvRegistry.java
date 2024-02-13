package com.alcineo.bonappetit.model;

public enum TlvRegistry {
    TAG_FILE_CONTROL_INFORMATION_6F (new byte[] { 0x6F }),
    TAG_FILE_CONTROL_INFORMATION_A5 (new byte[] {(byte) 0xA5}),
    TAG_FILE_CONTROL_INFORMATION_BF0C (new byte[] {(byte) 0xBF, 0x0C}),
    TAG_APPLICATION_TEMPLATE (new byte[] {0x61}),
    TAG_APPLICATION_ID (new byte[] {0x4F}),
    TAG_APPLICATION_LABEL (new byte[] {0x50}),
    TAG_APPLICATION_PRIORITY (new byte[] {(byte) 0x87}),
    TAG_APPLICATION_PREFERENCE (new byte[] {(byte) 0x9F, 0x2A})
    ;

    private final byte[] value;

    TlvRegistry(byte[] bytes) {
        this.value = bytes;
    }

    public byte[] getValue() {
        return value;
    }
}
