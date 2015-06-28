package com.orhanobut.wasp;

/**
 * @author Orhan Obut
 */
interface ImageHandler {

  void load(ImageCreator waspImageCreator);

  void clearCache();
}
