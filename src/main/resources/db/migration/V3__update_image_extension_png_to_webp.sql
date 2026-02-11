UPDATE content_page
SET content_value = REPLACE(content_value, '.png', '.webp')
WHERE content_type = 'IMAGE_URL'
  AND content_value LIKE '%.png';