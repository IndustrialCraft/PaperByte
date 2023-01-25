package com.github.industrialcraft.paperbyte.common.util;

import java.io.DataOutputStream;
import java.io.IOException;

public interface IStreamSerializable {
    void toStream(DataOutputStream stream) throws IOException;
}
