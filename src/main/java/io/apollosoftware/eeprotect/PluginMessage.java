package io.apollosoftware.eeprotect;

import io.apollosoftware.lib.lang.PrefixedMessage;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public class PluginMessage extends PrefixedMessage<EEProtect> {
    public PluginMessage(String key) {
        super(key);
    }
}
