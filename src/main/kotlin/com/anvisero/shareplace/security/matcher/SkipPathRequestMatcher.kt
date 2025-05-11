package com.anvisero.shareplace.security.matcher

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import java.util.stream.Collectors

class SkipPathRequestMatcher(pathsToSkip: MutableList<String>) : RequestMatcher {
    private val matchers: OrRequestMatcher

    init {
        val listOfPaths: MutableList<RequestMatcher> =
            pathsToSkip.stream().map { pattern: String -> AntPathRequestMatcher(pattern) }
                .collect(Collectors.toList())
        matchers = OrRequestMatcher(listOfPaths)
    }

    override fun matches(request: HttpServletRequest?): Boolean {
        return !matchers.matches(request)
    }
}