# Image Upload Custom View Integration

## Overview

This repository contains a custom Android view called `ImageUploadView` designed to simplify the process of selecting, previewing, and submitting images. This README provides instructions on integrating this custom view into your Android project.

## Integration Steps

1. **Add the Custom View to Your Project:**

   Copy the `ImageUploadView` class into your project's source code.

2. **Layout Integration:**

   In your XML layout file, add the following code to include the `ImageUploadView`:

   ```xml
   <com.example.sg.view.ImageUploadView
       android:id="@+id/imageUploadView"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"/>

3. **Initialization in Fragment or Activity:**

   In your Fragment or Activity, initialize and register the custom view in the `onCreateView` or `onCreate` method:

   ```kotlin
   override fun onCreateView(
       inflater: LayoutInflater, container: ViewGroup?,
       savedInstanceState: Bundle?
   ): View {
       // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_sample, container, false)
       view.findViewById<ImageUploadView>(R.id.imageUploadView)
           .initialiseRegisterForResult(requireActivity())
       return view
   }