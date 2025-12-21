package org.koitharu.kotatsu.parsers.site.en

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONObject
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.MangaSourceParser
import org.koitharu.kotatsu.parsers.config.ConfigKey
import org.koitharu.kotatsu.parsers.core.PagedMangaParser
import org.koitharu.kotatsu.parsers.exception.ParseException
import org.koitharu.kotatsu.parsers.model.*
import org.koitharu.kotatsu.parsers.util.*
import java.util.*
import java.math.BigDecimal
import java.math.RoundingMode

@MangaSourceParser("COMIX", "Comix", "en", ContentType.MANGA)
internal class Comix(context: MangaLoaderContext) :
	PagedMangaParser(context, MangaParserSource.COMIX, 28) {

	override val configKeyDomain = ConfigKey.Domain("comix.to")
	private val apiBase = "api/v2"
	private val apiBaseUrl get() = "https://$domain/$apiBase"

	override val filterCapabilities: MangaListFilterCapabilities
		get() = MangaListFilterCapabilities(
			isSearchSupported = true,
			isSearchWithFiltersSupported = true,
			isMultipleTagsSupported = true,
			isTagsExclusionSupported = false,
		)

	override val availableSortOrders: Set<SortOrder> = LinkedHashSet(
		listOf(
			SortOrder.RELEVANCE,
			SortOrder.UPDATED,
			SortOrder.POPULARITY,
			SortOrder.NEWEST,
			SortOrder.ALPHABETICAL
		)
	)

	override suspend fun getFilterOptions() = MangaListFilterOptions(
		availableTags = fetchAvailableTags()
	)

	private suspend fun fetchAvailableTags(): Set<MangaTag> {
		return setOf(
			// Genres
			MangaTag(key = "6", title = "Action", source = source),
			MangaTag(key = "87264", title = "Adult", source = source),
			MangaTag(key = "7", title = "Adventure", source = source),
			MangaTag(key = "8", title = "Boys Love", source = source),
			MangaTag(key = "9", title = "Comedy", source = source),
			MangaTag(key = "10", title = "Crime", source = source),
			MangaTag(key = "11", title = "Drama", source = source),
			MangaTag(key = "87265", title = "Ecchi", source = source),
			MangaTag(key = "12", title = "Fantasy", source = source),
			MangaTag(key = "13", title = "Girls Love", source = source),
			MangaTag(key = "87266", title = "Hentai", source = source),
			MangaTag(key = "14", title = "Historical", source = source),
			MangaTag(key = "15", title = "Horror", source = source),
			MangaTag(key = "16", title = "Isekai", source = source),
			MangaTag(key = "17", title = "Magical Girls", source = source),
			MangaTag(key = "87267", title = "Mature", source = source),
			MangaTag(key = "18", title = "Mecha", source = source),
			MangaTag(key = "19", title = "Medical", source = source),
			MangaTag(key = "20", title = "Mystery", source = source),
			MangaTag(key = "21", title = "Philosophical", source = source),
			MangaTag(key = "22", title = "Psychological", source = source),
			MangaTag(key = "23", title = "Romance", source = source),
			MangaTag(key = "24", title = "Sci-Fi", source = source),
			MangaTag(key = "25", title = "Slice of Life", source = source),
			MangaTag(key = "87268", title = "Smut", source = source),
			MangaTag(key = "26", title = "Sports", source = source),
			MangaTag(key = "27", title = "Superhero", source = source),
			MangaTag(key = "28", title = "Thriller", source = source),
			MangaTag(key = "29", title = "Tragedy", source = source),
			MangaTag(key = "30", title = "Wuxia", source = source),
			// Themes
			MangaTag(key = "31", title = "Aliens", source = source),
			MangaTag(key = "32", title = "Animals", source = source),
			MangaTag(key = "33", title = "Cooking", source = source),
			MangaTag(key = "34", title = "Crossdressing", source = source),
			MangaTag(key = "35", title = "Delinquents", source = source),
			MangaTag(key = "36", title = "Demons", source = source),
			MangaTag(key = "37", title = "Genderswap", source = source),
			MangaTag(key = "38", title = "Ghosts", source = source),
			MangaTag(key = "39", title = "Gyaru", source = source),
			MangaTag(key = "40", title = "Harem", source = source),
			MangaTag(key = "41", title = "Incest", source = source),
			MangaTag(key = "42", title = "Loli", source = source),
			MangaTag(key = "43", title = "Mafia", source = source),
			MangaTag(key = "44", title = "Magic", source = source),
			MangaTag(key = "45", title = "Martial Arts", source = source),
			MangaTag(key = "46", title = "Military", source = source),
			MangaTag(key = "47", title = "Monster Girls", source = source),
			MangaTag(key = "48", title = "Monsters", source = source),
			MangaTag(key = "49", title = "Music", source = source),
			MangaTag(key = "50", title = "Ninja", source = source),
			MangaTag(key = "51", title = "Office Workers", source = source),
			MangaTag(key = "52", title = "Police", source = source),
			MangaTag(key = "53", title = "Post-Apocalyptic", source = source),
			MangaTag(key = "54", title = "Reincarnation", source = source),
			MangaTag(key = "55", title = "Reverse Harem", source = source),
			MangaTag(key = "56", title = "Samurai", source = source),
			MangaTag(key = "57", title = "School Life", source = source),
			MangaTag(key = "58", title = "Shota", source = source),
			MangaTag(key = "59", title = "Supernatural", source = source),
			MangaTag(key = "60", title = "Survival", source = source),
			MangaTag(key = "61", title = "Time Travel", source = source),
			MangaTag(key = "62", title = "Traditional Games", source = source),
			MangaTag(key = "63", title = "Vampires", source = source),
			MangaTag(key = "64", title = "Video Games", source = source),
			MangaTag(key = "65", title = "Villainess", source = source),
			MangaTag(key = "66", title = "Virtual Reality", source = source),
			MangaTag(key = "67", title = "Zombies", source = source),
		)
	}

	// kotlin
	// kotlin
	override suspend fun getListPage(page: Int, order: SortOrder, filter: MangaListFilter): List<Manga> {
		val builder = "$apiBaseUrl/manga".toHttpUrl().newBuilder().apply {
			if (!filter.query.isNullOrBlank()) {
				addQueryParameter("keyword", filter.query)
			}

			val (param, dir) = when (order) {
				SortOrder.RELEVANCE -> "relevance" to "desc"
				SortOrder.UPDATED -> "chapter_updated_at" to "desc"
				SortOrder.POPULARITY -> "views_30d" to "desc"
				SortOrder.NEWEST -> "created_at" to "desc"
				SortOrder.ALPHABETICAL -> "title" to "asc"
				else -> "chapter_updated_at" to "desc"
			}
			addQueryParameter("order[$param]", dir)

			if (filter.tags.isNotEmpty()) {
				filter.tags.forEach { addQueryParameter("genres[]", it.key) }
			}
			if (filter.tagsExclude.isNotEmpty()) {
				filter.tagsExclude.forEach { addQueryParameter("genres[]", "-${it.key}") }
			}

			if (filter.tags.isEmpty() && filter.tagsExclude.isEmpty()) {
				nsfwGenreIds.forEach { addQueryParameter("genres[]", "-$it") }
			}

			addQueryParameter("limit", pageSize.toString())
			addQueryParameter("page", page.toString())
		}

		val response = webClient.httpGet(builder.build()).parseJson()
		val items = response.optJSONObject("result")?.optJSONArray("items") ?: return emptyList()
		val list = ArrayList<Manga>(items.length())

		for (i in 0 until items.length()) {
			val it = items.optJSONObject(i) ?: continue
			list.add(parseMangaFromJson(it))
		}
		return list
	}

	private fun parseMangaFromJson(json: JSONObject): Manga {
		val hashId = json.optString("hash_id", "").nullIfEmpty()
		val slug = json.optString("slug", "").nullIfEmpty()
		val title = json.optString("title", "Unknown")
		val description = json.optString("synopsis", "").nullIfEmpty()
		val poster = json.getJSONObject("poster")
		val coverUrl = poster.optString("large", "").nullIfEmpty()
		val status = json.optString("status", "")
		val year = json.optInt("year", 0)
		val rating = json.optDouble("rated_avg", 0.0)

		val poster = json.optJSONObject("poster")
		val coverUrl = poster?.optString("medium", "")?.nullIfEmpty()
			?: poster?.optString("large", "")?.nullIfEmpty()
			?: poster?.optString("small", "")?.nullIfEmpty()
			?: ""

		val state = when (json.optString("status", "").lowercase()) {
			"finished" -> MangaState.FINISHED
			"releasing" -> MangaState.ONGOING
			"on_hiatus" -> MangaState.PAUSED
			"discontinued" -> MangaState.ABANDONED
			else -> null
		}

		val ratedAvg = json.optDouble("rated_avg", 0.0)
		val rating = if (ratedAvg > 0.0) (ratedAvg / 20.0).toFloat() else RATING_UNKNOWN

		val resolvedHash = hashId?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
		val urlSlug = if (slug != null) "$resolvedHash-$slug" else resolvedHash

		return Manga(
			id = generateUid(resolvedHash),
			url = "/title/$urlSlug",
			publicUrl = "https://$domain/title/$urlSlug",
			coverUrl = coverUrl,
			title = title,
			altTitles = emptySet(),
			description = description,
			rating = rating,
			tags = emptySet(),
			authors = emptySet(),
			state = state,
			source = source,
			contentRating = if (json.optBoolean("is_nsfw", false)) ContentRating.ADULT else ContentRating.SAFE
		)
	}

	// -------------------------
	// Details
	// -------------------------
	override suspend fun getDetails(manga: Manga): Manga = coroutineScope {
		val hash = manga.url.substringAfter("/title/").substringBefore("-").nullIfEmpty()
			?: throw ParseException("Invalid manga URL", manga.url)

		val detailsUrl = "$apiBaseUrl/manga/$hash".toHttpUrl().newBuilder()
			.addQueryParameter("includes[]", "author")
			.addQueryParameter("includes[]", "artist")
			.addQueryParameter("includes[]", "genre")
			.addQueryParameter("includes[]", "theme")
			.addQueryParameter("includes[]", "demographic")
			.build()
		val detailsDeferred = async { webClient.httpGet(detailsUrl).parseJson() }
		val chaptersDeferred = async { getChapters(hash) }

		val response = try {
			detailsDeferred.await()
		} catch (_: Exception) {
			JSONObject()
		}
		val chapters = try {
			chaptersDeferred.await()
		} catch (_: Exception) {
			emptyList()
		}

		val result = response.optJSONObject("result")
		if (result != null) {
			val updated = parseMangaFromJson(result)

			val authors = LinkedHashSet<String>()
			result.optJSONArray("author")?.let { arr ->
				for (i in 0 until arr.length()) {
					arr.optJSONObject(i)?.optString("title")?.nullIfEmpty()?.let { authors.add(it) }
				}
			}
			result.optJSONArray("artist")?.let { arr ->
				for (i in 0 until arr.length()) {
					arr.optJSONObject(i)?.optString("title")?.nullIfEmpty()?.let { authors.add(it) }
				}
			}

			val tags = mutableSetOf<MangaTag>()
			fun addTags(field: String) {
				result.optJSONArray(field)?.let { arr ->
					for (i in 0 until arr.length()) {
						val o = arr.optJSONObject(i) ?: continue
						val name = o.optString("title", "").nullIfEmpty() ?: continue
						val id = o.optInt("term_id", 0).takeIf { it != 0 }?.toString() ?: continue
						tags.add(MangaTag(key = id, title = name, source = source))
					}
				}
			}
			addTags("genre"); addTags("theme"); addTags("demographic")

			val ratedAvg = result.optDouble("rated_avg", 0.0)
			val fancyScore = generateFancyScore(ratedAvg)
			val synopsis = result.optString("synopsis", "")
			val altTitles = result.optJSONArray("alt_titles")?.let { arr ->
				(0 until arr.length()).map { arr.getString(it) }
			} ?: emptyList()

			val newDesc = buildString {
				if (fancyScore.isNotEmpty()) {
					append(fancyScore).append("\n\n")
				}
				append(synopsis)
				if (altTitles.isNotEmpty()) {
					append("\n\nAlternative Names:\n")
					append(altTitles.joinToString("\n"))
				}
			}

			return@coroutineScope updated.copy(
				chapters = chapters,
				authors = authors,
				tags = tags,
				description = newDesc,
				altTitles = altTitles.toSet()
			)
		}

		return@coroutineScope manga.copy(chapters = chapters)
	}

	override suspend fun getRelatedManga(seed: Manga): List<Manga> = emptyList()

	override suspend fun getPages(chapter: MangaChapter): List<MangaPage> {
		val chapterId = chapter.url.substringAfterLast("/").substringBefore("-")
		val response = webClient.httpGet("$apiBaseUrl/chapters/$chapterId").parseJson()

					val imagesJsonString = scriptContent.substring(arrayStart, arrayEnd)
					// Parse the JSON array, handling escaped quotes
					images = JSONArray(imagesJsonString.replace("\\\"", "\""))
					break
				} catch (e: Exception) {
					// Continue to next script if parsing fails
					continue
				}
			}
		}

		for (i in 0 until images.length()) {
			val imgObj = images.optJSONObject(i) ?: continue
			val url = imgObj.optString("url", "").nullIfEmpty() ?: continue

		return (0 until images.length()).map { i ->
			val imageItem = images.get(i)
			val imageUrl = when (imageItem) {
				is String -> imageItem
				is JSONObject -> imageItem.getString("url")
				else -> throw ParseException("Unexpected image format", chapterUrl)
			}
			MangaPage(
				id = generateUid("$chapterId-$i"),
				url = imageUrl,
				preview = null,
				source = source
			))
		}

		return pages
	}

	private suspend fun getChapters(manga: Manga): List<MangaChapter> {
		val hashId = manga.url.substringAfter("/title/")
		val allChapters = mutableListOf<JSONObject>()
		var page = 1

		// Fetch all chapters with pagination
		while (true) {
			val chaptersUrl = "https://comix.to/api/v2/manga/$hashId/chapters?order[number]=desc&limit=100&page=$page"
			val response = webClient.httpGet(chaptersUrl).parseJson()
			val result = response.getJSONObject("result")
			val items = result.getJSONArray("items")

			if (items.length() == 0) break

			for (i in 0 until items.length()) {
				allChapters.add(items.getJSONObject(i))
			}

			// Check pagination info to see if we have more pages
			val pagination = result.optJSONObject("pagination")
			if (pagination != null) {
				val currentPage = pagination.getInt("current_page")
				val lastPage = pagination.getInt("last_page")
				if (currentPage >= lastPage) break
			}

			page++
		}

		// Group chapters by number and pick one translation per chapter (preferring latest)
		val uniqueChapters = allChapters
			.groupBy { it.getDouble("number") }
			.mapValues { (_, chapters) ->
				// Sort by creation date descending and take the first (most recent)
				chapters.maxByOrNull { it.getLong("created_at") }!!
			}
			.values
			.sortedByDescending { it.getDouble("number") } // Sort by chapter number descending

		return uniqueChapters.mapIndexedNotNull { index, item ->
			val chapterId = item.getLong("chapter_id")
			val number = item.getDouble("number").toFloat()
			val name = item.optString("name", "").nullIfEmpty()
			val createdAt = item.getLong("created_at")
			val scanlationGroup = item.optJSONObject("scanlation_group")
			val scanlatorName = scanlationGroup?.optString("name", null)

			val title = if (name != null) {
				"Chapter $number: $name"
			} else {
				"Chapter $number"
			}

			MangaChapter(
				id = generateUid(chapterId.toString()),
				title = title,
				number = number,
				volume = 0,
				url = "/title/$hashId/$chapterId-chapter-${number.toInt()}",
				uploadDate = createdAt * 1000L, // Convert to milliseconds
				source = source,
				scanlator = scanlatorName,
				branch = null,
			)
		}.reversed() // Reverse to have ascending order
	}
}
