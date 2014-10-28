/**
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.codehaus.swizzle.stream;

public interface PushbackBuffer {
    /**
     * Push the bytes on to the head of the buffer, so they are the next bytes
     * returned.
     */
    void unread(byte[] bytes);

    /**
     * Push the bytes on to the head of the buffer, so they are the next bytes
     * returned.
     */
    void unread(byte[] bytes, int off, int len);

    /**
     * Gets the current contents of the buffer without modifying the position.
     */
    byte[] getBuffer();
}
