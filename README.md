[![CodeFactor](https://www.codefactor.io/repository/github/steventheperson/brillianceprivate/badge?s=c0a5eaf88c7b034e40602a1af5b270816062ecc1)](https://www.codefactor.io/repository/github/steventheperson/brillianceprivate)
# 🎨 brilliance

A constructor for extracting colors from images written in Java. Quickly retrieve common and prominent colors from images.

### Example

![App Screenshot](https://images.unsplash.com/photo-1678285624327-3ec8a48e1609?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=340&q=80)\
*[via Frida Lannerström on Unsplash](https://unsplash.com/@fridalannerstrom)*

[p]: https://via.placeholder.com/10/ff9e13?text=+
[c]: https://via.placeholder.com/10/90c1c5?text=+

|           | Result                         | Tolerance |
|-----------|--------------------------------|-----------|
| Prominent | ![][p]![][p]![][p]![][p]![][p] | 80        |
| Common    | ![][c]![][c]![][c]![][c]![][c] | 1         |

### Usage

```java
    // Create the constructor
    Brilliance brilliance = new Brilliance()
            // Image to search; String, URL, BufferedImage.
            .image("https://images.unsplash.com/photo-1678285624327-3ec8a48e1609?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8")
            // How much gray-tone a pixel can have. (1 = most common color, higher = more prominent)
            .grayTolerance(80)
            // Quality of the blocking/splitting of the image. 1 is the highest quality.
            .quality(1)
            // Include all pixels in the search.
            .includeGray(false)
            // Speed at which the gray tolerance will chunk in the while loop when a color is null.
            .speed(2)
            // Automatically scales the quality setting the higher the image pixel count.
            .scale(true)
            // Log statistics from the color search.
            .log(true);
    // Build the constructor.
    Color color = brilliance.build();
```
### TODO
- Improvements to documentation.
- Gradle repo